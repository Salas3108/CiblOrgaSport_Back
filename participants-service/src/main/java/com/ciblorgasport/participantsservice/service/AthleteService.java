package com.ciblorgasport.participantsservice.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ciblorgasport.participantsservice.dto.AthleteMapper;
import com.ciblorgasport.participantsservice.dto.EquipeDto;
import com.ciblorgasport.participantsservice.dto.request.UpdateAthleteDocsRequest;
import com.ciblorgasport.participantsservice.dto.request.UpdateAthleteInfoRequest;
import com.ciblorgasport.participantsservice.dto.request.UpdateAthleteObservationRequest;
import com.ciblorgasport.participantsservice.dto.request.ValidationRequest;
import com.ciblorgasport.participantsservice.model.Athlete;
import com.ciblorgasport.participantsservice.model.AthleteDocs;
import com.ciblorgasport.participantsservice.model.Equipe;
import com.ciblorgasport.participantsservice.model.Message;
import com.ciblorgasport.participantsservice.repository.JpaAthleteRepository;
import com.ciblorgasport.participantsservice.repository.JpaMessageRepository;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class AthleteService {

    private final JpaAthleteRepository athleteRepository;
    private final JpaMessageRepository messageRepository;
    private final AthleteMapper athleteMapper;
    private final ParticipantsStore store;

    public AthleteService(JpaAthleteRepository athleteRepository,
                          JpaMessageRepository messageRepository,
                          AthleteMapper athleteMapper,
                          ParticipantsStore store) {
        this.athleteRepository = athleteRepository;
        this.messageRepository = messageRepository;
        this.athleteMapper = athleteMapper;
        this.store = store;
    }

    public List<Athlete> findAll() {
        return athleteRepository.findAll();
    }

    public List<Athlete> findValidated() {
        return athleteRepository.findByValideTrue();
    }

    public boolean existsById(Long id) {
        return athleteRepository.existsById(id);
    }

    public Athlete findByIdOrThrow(Long id) {
        return athleteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Athlète introuvable: " + id));
    }

    public Athlete updateUsernameIfMissing(Long id, String username) {
        Athlete athlete = findByIdOrThrow(id);
        if (athlete.getUsername() == null || athlete.getUsername().isBlank()) {
            if (username != null && !username.isBlank()) {
                athlete.setUsername(username);
                return athleteRepository.save(athlete);
            }
        }
        return athlete;
    }

    @Transactional(readOnly = true)
    public EquipeDto getEquipeForAthlete(Long athleteId) {
        Athlete athlete = findByIdOrThrow(athleteId);
        Equipe equipe = athlete.getEquipe();
        if (equipe == null) {
            return null;
        }

        EquipeDto dto = new EquipeDto();
        dto.setId(equipe.getId());
        dto.setNom(equipe.getNom());
        dto.setPays(equipe.getPays());

        if (equipe.getAthletes() != null) {
            Map<Long, String> idUsernameMap = equipe.getAthletes().stream()
                    .filter(a -> !a.getId().equals(athleteId)) // exclut l'athlète courant si nécessaire
                    .collect(Collectors.toMap(
                            Athlete::getId,
                            a -> a.getUsername() != null ? a.getUsername() : ""
                    ));
            dto.setAthleteIdUsernameMap(idUsernameMap);
        }

        return dto;
    }

    /**
     * Retourne l'athlète existant ou lève une exception si introuvable.
     * Nous n'acceptons plus la création implicite depuis les endpoints ATHLETE.
     */
    private Athlete findExistingAthleteOrThrow(Long id) {
        return athleteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Athlète introuvable: " + id));
    }

    public Athlete updateInfo(Long id, UpdateAthleteInfoRequest request) {
        Athlete athlete = findExistingAthleteOrThrow(id);

        // On met à jour uniquement les champs présents
        if (request.getNom() != null) athlete.setNom(request.getNom());
        if (request.getPrenom() != null) athlete.setPrenom(request.getPrenom());
        if (request.getDateNaissance() != null) athlete.setDateNaissance(request.getDateNaissance());
        if (request.getPays() != null) athlete.setPays(request.getPays());
        if (request.getSexe() != null) athlete.setSexe(request.getSexe());

        store.addLog("ATHLETE updateInfo id=" + id);
        return athleteRepository.save(athlete);
    }

    public Athlete updateDocs(Long id, UpdateAthleteDocsRequest request) {
        Athlete athlete = findExistingAthleteOrThrow(id);
        if (request.getDocs() == null) {
            throw new IllegalArgumentException("docs est obligatoire");
        }
        athlete.setDocs(athleteMapper.toEntity(request.getDocs()));
        store.addLog("ATHLETE updateDocs id=" + id);
        return athleteRepository.save(athlete);
    }

    public Athlete updateDocsFiles(Long id, byte[] certificatMedicalPdf, byte[] passportPdf) {
        Athlete athlete = findExistingAthleteOrThrow(id);

        if ((certificatMedicalPdf == null || certificatMedicalPdf.length == 0)
                && (passportPdf == null || passportPdf.length == 0)) {
            throw new IllegalArgumentException("au moins un document PDF est obligatoire");
        }

        AthleteDocs docs = athlete.getDocs();
        if (docs == null) {
            docs = new AthleteDocs();
        }

        if (certificatMedicalPdf != null && certificatMedicalPdf.length > 0) {
            docs.setCertificatMedical(certificatMedicalPdf);
        }
        if (passportPdf != null && passportPdf.length > 0) {
            docs.setPassport(passportPdf);
        }

        athlete.setDocs(docs);
        store.addLog("ATHLETE updateDocsFiles id=" + id);
        return athleteRepository.save(athlete);
    }

    public Athlete updateObservation(Long id, UpdateAthleteObservationRequest request) {
        Athlete athlete = findExistingAthleteOrThrow(id);
        athlete.setObservation(request.getObservation());
        store.addLog("ATHLETE updateObservation id=" + id);
        return athleteRepository.save(athlete);
    }

    public Athlete validate(Long id, ValidationRequest request) {
        Athlete athlete = findByIdOrThrow(id);
        enforceCompletenessBeforeValidation(athlete);
        athlete.setValide(request.isValide());

        // Si validé, on efface le motif de refus
        if (request.isValide()) {
            athlete.setMotifRefus(null);
        } else {
            athlete.setMotifRefus(request.getMotifRefus());
        }

        // Optionnel: garder un message dans l'historique
        if (request.getMessage() != null && !request.getMessage().isBlank()) {
            Message message = new Message(null, id, request.getMessage(), LocalDateTime.now());
            messageRepository.save(message);
        }

        store.addLog("COMMISSAIRE validate id=" + id + " => " + request.isValide());
        return athleteRepository.save(athlete);
    }

    private void enforceCompletenessBeforeValidation(Athlete athlete) {
        if (athlete.getNom() == null || athlete.getNom().isBlank()) {
            throw new IllegalArgumentException("nom est obligatoire pour valider");
        }
        if (athlete.getPrenom() == null || athlete.getPrenom().isBlank()) {
            throw new IllegalArgumentException("prenom est obligatoire pour valider");
        }
        if (athlete.getDateNaissance() == null) {
            throw new IllegalArgumentException("dateNaissance est obligatoire pour valider");
        }
        if (athlete.getPays() == null || athlete.getPays().isBlank()) {
            throw new IllegalArgumentException("pays est obligatoire pour valider");
        }
        if (athlete.getDocs() == null) {
            throw new IllegalArgumentException("documents sont obligatoires pour valider");
        }
        if (athlete.getDocs().getCertificatMedical() == null || athlete.getDocs().getCertificatMedical().length == 0) {
            throw new IllegalArgumentException("certificatMedical est obligatoire pour valider");
        }
        if (athlete.getDocs().getPassport() == null || athlete.getDocs().getPassport().length == 0) {
            throw new IllegalArgumentException("passport est obligatoire pour valider");
        }
        if (athlete.getSexe() == null) {
            throw new IllegalArgumentException("Le genre (sexe) est obligatoire pour valider le profil");
        }
        if (athlete.getDocs().getDocumentGenre() == null || athlete.getDocs().getDocumentGenre().isBlank()) {
            throw new IllegalArgumentException("Le document justificatif de genre est obligatoire pour valider le profil");
        }
    }

    public Message createMessage(Long athleteId, String contenu) {
        if (contenu == null || contenu.isBlank()) {
            throw new IllegalArgumentException("contenu est obligatoire");
        }
        // Vérifie que l'athlète existe
        findByIdOrThrow(athleteId);

        Message message = new Message(null, athleteId, contenu, LocalDateTime.now());
        store.addLog("COMMISSAIRE message athleteId=" + athleteId);
        return messageRepository.save(message);
    }

    /**
     * Create an Athlete for a given authenticated user if missing. Idempotent.
     *
     * In this microservice, we align athleteId == userId.
     */
    public Athlete createIfMissingForUser(Long userId, String username) {
        if (userId == null) {
            throw new IllegalArgumentException("userId est obligatoire");
        }
        return athleteRepository.findById(userId).map(existing -> {
            if ((existing.getUsername() == null || existing.getUsername().isBlank())
                    && username != null && !username.isBlank()) {
                existing.setUsername(username);
                return athleteRepository.save(existing);
            }
            return existing;
        }).orElseGet(() -> {
            Athlete athlete = new Athlete();
            athlete.setId(userId);
            if (username != null && !username.isBlank()) {
                athlete.setUsername(username);
            }
            athlete.setValide(false);
            athlete.setDocs(new AthleteDocs(null, null));
            athlete.setObservation(null);
            return athleteRepository.save(athlete);
        });
    }

    /**
     * Récupère le PDF d'un athlète pour téléchargement.
     * @param athleteId ID de l'athlète
     * @param docType "certificatMedical" ou "passport"
     * @return Contenu du PDF en tant que byte[]
     */
    public byte[] getPdfBytes(Long athleteId, String docType) {
        Athlete athlete = findByIdOrThrow(athleteId);
        if (athlete.getDocs() == null) {
            throw new IllegalArgumentException("Pas de document trouvé");
        }

        byte[] pdfBytes;
        if ("certificatMedical".equalsIgnoreCase(docType)) {
            pdfBytes = athlete.getDocs().getCertificatMedical();
        } else if ("passport".equalsIgnoreCase(docType)) {
            pdfBytes = athlete.getDocs().getPassport();
        } else {
            throw new IllegalArgumentException("Type de document invalide: " + docType);
        }

        if (pdfBytes == null || pdfBytes.length == 0) {
            throw new IllegalArgumentException("Document " + docType + " non trouvé");
        }

        return pdfBytes;
    }
}
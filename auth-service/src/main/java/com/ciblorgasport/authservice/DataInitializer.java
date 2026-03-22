package com.ciblorgasport.authservice;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.ciblorgasport.authservice.entity.Role;
import com.ciblorgasport.authservice.entity.User;
import com.ciblorgasport.authservice.repository.UserRepository;

/**
 * Initialise la base de données avec des comptes de démonstration au démarrage.
 * Idempotent : vérifie l'existence avant toute insertion.
 */
@Component
public class DataInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);
    private static final String DEFAULT_PASSWORD = "password123";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        String encodedPassword = passwordEncoder.encode(DEFAULT_PASSWORD);

        createIfAbsent("admin1",        "admin1@ciblorgasport.com",        encodedPassword, Role.ADMIN,        true);
        createIfAbsent("commissaire1",  "commissaire1@ciblorgasport.com",  encodedPassword, Role.COMMISSAIRE, true);
        createIfAbsent("commissaire2",  "commissaire2@ciblorgasport.com",  encodedPassword, Role.COMMISSAIRE, true);

        List.of(
            new String[]{"athlete01", "Mohamed",     "Ben Ali",         "Tunisie",   "athlete01@ciblorgasport.com"},
            new String[]{"athlete02", "Léa",         "Martin",          "France",    "athlete02@ciblorgasport.com"},
            new String[]{"athlete03", "Carlos",      "Ruiz",            "Espagne",   "athlete03@ciblorgasport.com"},
            new String[]{"athlete04", "Sofia",       "Greco",           "Italie",    "athlete04@ciblorgasport.com"},
            new String[]{"athlete05", "Youssef",     "Amrani",          "Maroc",     "athlete05@ciblorgasport.com"},
            new String[]{"athlete06", "Elena",       "Petrakis",        "Grèce",     "athlete06@ciblorgasport.com"},
            new String[]{"athlete07", "Mehmet",      "Yilmaz",          "Turquie",   "athlete07@ciblorgasport.com"},
            new String[]{"athlete08", "Amira",       "Khalil",          "Algérie",   "athlete08@ciblorgasport.com"},
            new String[]{"athlete09", "João",        "Santos",          "Portugal",  "athlete09@ciblorgasport.com"},
            new String[]{"athlete10", "Marta",       "Kovac",           "Croatie",   "athlete10@ciblorgasport.com"},
            new String[]{"athlete11", "Lucas",       "Dubois",          "France",    "athlete11@ciblorgasport.com"},
            new String[]{"athlete12", "Fatima",      "Zahra Benali",    "Maroc",     "athlete12@ciblorgasport.com"},
            new String[]{"athlete13", "Marco",       "Rossi",           "Italie",    "athlete13@ciblorgasport.com"},
            new String[]{"athlete14", "Nadia",       "Bouchard",        "France",    "athlete14@ciblorgasport.com"},
            new String[]{"athlete15", "Karim",       "Mansouri",        "Tunisie",   "athlete15@ciblorgasport.com"},
            new String[]{"athlete16", "Ana",         "Gomez",           "Espagne",   "athlete16@ciblorgasport.com"},
            new String[]{"athlete17", "Stavros",     "Nikolaou",        "Grèce",     "athlete17@ciblorgasport.com"},
            new String[]{"athlete18", "Hana",        "Mujic",           "Croatie",   "athlete18@ciblorgasport.com"},
            new String[]{"athlete19", "Omar",        "Ferhat",          "Algérie",   "athlete19@ciblorgasport.com"},
            new String[]{"athlete20", "Inês",        "Fernandez",       "Portugal",  "athlete20@ciblorgasport.com"}
        ).forEach(data -> createIfAbsent(data[0], data[4], encodedPassword, Role.ATHLETE, true));

        log.info("[DataInitializer] Base de données initialisée : 1 admin, 2 commissaires, 20 athlètes.");
    }

    private void createIfAbsent(String username, String email, String encodedPassword, Role role, boolean validated) {
        if (userRepository.existsByUsername(username)) {
            return;
        }
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(encodedPassword);
        user.setRole(role);
        user.setValidated(validated);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        log.debug("[DataInitializer] Créé : {} ({})", username, role);
    }
}

package com.ciblorgasport.dto;

import org.springframework.stereotype.Component;

import com.ciblorgasport.entity.Abonnement;

@Component
public class AbonnementMapper {

    public AbonnementDTO toDto(Abonnement a) {
        if (a == null) return null;
        AbonnementDTO dto = new AbonnementDTO();
        dto.setId(a.getId());
        dto.setUserId(a.getUserId());
        dto.setCompetitionId(a.getCompetitionId());
        dto.setDateAbonnement(a.getDateAbonnement());
        dto.setNotificationsActives(a.isNotificationsActives());
        dto.setStatus(a.getStatus());
        return dto;
    }

    public Abonnement toEntity(AbonnementDTO dto) {
        if (dto == null) return null;
        Abonnement a = new Abonnement();
        a.setId(dto.getId());
        a.setUserId(dto.getUserId());
        a.setCompetitionId(dto.getCompetitionId());
        a.setDateAbonnement(dto.getDateAbonnement());
        a.setNotificationsActives(dto.isNotificationsActives());
        a.setStatus(dto.getStatus());
        return a;
    }
}

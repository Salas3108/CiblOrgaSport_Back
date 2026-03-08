package com.ciblorgasport.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AbonnementTest {

    @Test
    void testNoArgsConstructor() {
        Abonnement abonnement = new Abonnement();
      
        assertThat(abonnement).isNotNull();
        assertThat(abonnement.getDateAbonnement()).isNotNull();
        assertThat(abonnement.isNotificationsActives()).isTrue();
        assertThat(abonnement.getStatus()).isEqualTo(AbonnementStatus.ACTIF);
    }

    @Test
    void testAllArgsConstructor() {
        Long userId = 123L;
        Long competitionId = 42L;
        
        Abonnement abonnement = new Abonnement(userId, competitionId);

        assertThat(abonnement.getUserId()).isEqualTo(userId);
        assertThat(abonnement.getCompetitionId()).isEqualTo(competitionId);
        assertThat(abonnement.getDateAbonnement()).isNotNull();
        assertThat(abonnement.isNotificationsActives()).isTrue();
        assertThat(abonnement.getStatus()).isEqualTo(AbonnementStatus.ACTIF);
    }

    @Test
    void testGettersAndSetters() {
        Abonnement abonnement = new Abonnement();
        UUID id = UUID.randomUUID();
        Long userId = 456L;
        Long competitionId = 99L;
        LocalDateTime dateAbonnement = LocalDateTime.now().minusDays(2);
        
        abonnement.setId(id);
        abonnement.setUserId(userId);
        abonnement.setCompetitionId(competitionId);
        abonnement.setDateAbonnement(dateAbonnement);
        abonnement.setNotificationsActives(false);
        abonnement.setStatus(AbonnementStatus.SUSPENDU);
        
        assertThat(abonnement.getId()).isEqualTo(id);
        assertThat(abonnement.getUserId()).isEqualTo(userId);
        assertThat(abonnement.getCompetitionId()).isEqualTo(competitionId);
        assertThat(abonnement.getDateAbonnement()).isEqualTo(dateAbonnement);
        assertThat(abonnement.isNotificationsActives()).isFalse();
        assertThat(abonnement.getStatus()).isEqualTo(AbonnementStatus.SUSPENDU);
    }

    @Test
    void testDefaultValues() {
        Abonnement abonnement = new Abonnement();
        
        assertThat(abonnement.isNotificationsActives()).isTrue();
        assertThat(abonnement.getStatus()).isEqualTo(AbonnementStatus.ACTIF);
        assertThat(abonnement.getDateAbonnement()).isNotNull();
    }

    @Test
    void testConstructorSetsDateAutomatically() {
        LocalDateTime beforeCreation = LocalDateTime.now();
        
        Abonnement abonnement = new Abonnement(100L, 7L);
        
        assertThat(abonnement.getDateAbonnement()).isNotNull();
        assertThat(abonnement.getDateAbonnement()).isAfterOrEqualTo(beforeCreation);
    }

    @Test
    void testAbonnementStatusEnum() {
        assertThat(AbonnementStatus.values())
                .hasSize(3)
                .containsExactly(AbonnementStatus.ACTIF, AbonnementStatus.DESABONNE, AbonnementStatus.SUSPENDU);
        
        assertThat(AbonnementStatus.ACTIF.toString()).isEqualTo("ACTIF");
        assertThat(AbonnementStatus.DESABONNE.toString()).isEqualTo("DESABONNE");
        assertThat(AbonnementStatus.SUSPENDU.toString()).isEqualTo("SUSPENDU");
    }
}
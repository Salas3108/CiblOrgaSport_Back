package com.ciblorgasport.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AbonnementTest {

    @Test
    void testNoArgsConstructor() {
        // When
        Abonnement abonnement = new Abonnement();
        
        // Then
        assertThat(abonnement).isNotNull();
        assertThat(abonnement.getDateAbonnement()).isNotNull();
        assertThat(abonnement.isNotificationsActives()).isTrue();
        assertThat(abonnement.getStatus()).isEqualTo(AbonnementStatus.ACTIF);
    }

    @Test
    void testAllArgsConstructor() {
        // Given
        Long userId = 123L;
        UUID competitionId = UUID.randomUUID();
        
        // When
        Abonnement abonnement = new Abonnement(userId, competitionId);
        
        // Then
        assertThat(abonnement.getUserId()).isEqualTo(userId);
        assertThat(abonnement.getCompetitionId()).isEqualTo(competitionId);
        assertThat(abonnement.getDateAbonnement()).isNotNull();
        assertThat(abonnement.isNotificationsActives()).isTrue();
        assertThat(abonnement.getStatus()).isEqualTo(AbonnementStatus.ACTIF);
    }

    @Test
    void testGettersAndSetters() {
        // Given
        Abonnement abonnement = new Abonnement();
        UUID id = UUID.randomUUID();
        Long userId = 456L;
        UUID competitionId = UUID.randomUUID();
        LocalDateTime dateAbonnement = LocalDateTime.now().minusDays(2);
        
        // When
        abonnement.setId(id);
        abonnement.setUserId(userId);
        abonnement.setCompetitionId(competitionId);
        abonnement.setDateAbonnement(dateAbonnement);
        abonnement.setNotificationsActives(false);
        abonnement.setStatus(AbonnementStatus.SUSPENDU);
        
        // Then
        assertThat(abonnement.getId()).isEqualTo(id);
        assertThat(abonnement.getUserId()).isEqualTo(userId);
        assertThat(abonnement.getCompetitionId()).isEqualTo(competitionId);
        assertThat(abonnement.getDateAbonnement()).isEqualTo(dateAbonnement);
        assertThat(abonnement.isNotificationsActives()).isFalse();
        assertThat(abonnement.getStatus()).isEqualTo(AbonnementStatus.SUSPENDU);
    }

    @Test
    void testDefaultValues() {
        // When
        Abonnement abonnement = new Abonnement();
        
        // Then
        assertThat(abonnement.isNotificationsActives()).isTrue();
        assertThat(abonnement.getStatus()).isEqualTo(AbonnementStatus.ACTIF);
        assertThat(abonnement.getDateAbonnement()).isNotNull();
    }

    @Test
    void testConstructorSetsDateAutomatically() {
        // Given
        LocalDateTime beforeCreation = LocalDateTime.now();
        
        // When
        Abonnement abonnement = new Abonnement(100L, UUID.randomUUID());
        
        // Then
        assertThat(abonnement.getDateAbonnement()).isNotNull();
        assertThat(abonnement.getDateAbonnement()).isAfterOrEqualTo(beforeCreation);
    }

    @Test
    void testAbonnementStatusEnum() {
        // Test enum values
        assertThat(AbonnementStatus.values())
                .hasSize(3)
                .containsExactly(AbonnementStatus.ACTIF, AbonnementStatus.DESABONNE, AbonnementStatus.SUSPENDU);
        
        // Test enum string representation
        assertThat(AbonnementStatus.ACTIF.toString()).isEqualTo("ACTIF");
        assertThat(AbonnementStatus.DESABONNE.toString()).isEqualTo("DESABONNE");
        assertThat(AbonnementStatus.SUSPENDU.toString()).isEqualTo("SUSPENDU");
    }
}
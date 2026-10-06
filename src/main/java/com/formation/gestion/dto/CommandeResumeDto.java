package com.formation.gestion.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Module 4 : projection DTO (lecture seule, pas d'entite chargee). */
public record CommandeResumeDto(Long id, String reference, LocalDateTime dateCommande,
                                String nomClient, BigDecimal montant) {
}

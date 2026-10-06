package com.formation.gestion.dto;

import java.math.BigDecimal;

/** Module 9 : chiffre d'affaires par client (null -> 0 pour les clients sans commande). */
public record ClientCaDto(String nom, Long nbCommandes, BigDecimal chiffreAffaires) {

    public ClientCaDto {
        if (chiffreAffaires == null) {
            chiffreAffaires = BigDecimal.ZERO;
        }
    }
}

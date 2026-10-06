package com.formation.gestion.dto;

import com.formation.gestion.entity.Client;

/** Module 8 : ce qui sort du service. On n'expose jamais l'entite elle-meme. */
public record ClientDto(Long id, String nom, String email, String telephone, String ville, String statut) {

    public static ClientDto from(Client c) {
        return new ClientDto(c.getId(), c.getNom(), c.getEmail(), c.getTelephone(),
                c.getAdresse() == null ? null : c.getAdresse().getVille(),
                c.getStatut().name());
    }
}

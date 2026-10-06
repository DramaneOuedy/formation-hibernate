package com.formation.gestion;

import com.formation.gestion.entity.Commande;
import com.formation.gestion.entity.StatutClient;
import com.formation.gestion.service.JeuDeDonneesService;
import com.formation.gestion.service.Tp4Service;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Module4RequetesTest extends BaseTest {

    @Autowired
    JeuDeDonneesService jeu;

    @Autowired
    Tp4Service service;

    @Test
    void requetesDuFilRouge() {
        JeuDeDonneesService.Catalogue cat = jeu.creerClientsEtCatalogue();
        jeu.creerCommandes(12);

        assertTrue(service.clientParEmail("m.traore@exemple.ci").isPresent());
        assertEquals(4, service.commandesDuClient(cat.clientId()).size());
        assertEquals(6, service.commandesEntre(LocalDateTime.now().minusDays(16), LocalDateTime.now()).size());
        assertEquals(12, service.commandesAuDessusDe(BigDecimal.ZERO).size());
        assertEquals(12, service.compterCommandes());
        assertEquals(5, service.page(1, 5).size());
        assertEquals(1, service.rechercherClients(null, "Abidjan", StatutClient.ACTIF).size());
        assertEquals(1, service.produitsEnRupture(45).size());

        Long id = service.page(0, 1).get(0).getId();
        Commande detail = service.detailComplet(id).orElseThrow();
        assertEquals(2, detail.getLignes().size(), "lignes chargees par JOIN FETCH, utilisables hors transaction");
        assertTrue(detail.getLignes().get(0).getProduit().getNom().length() > 0);

        assertEquals(3, service.augmenterPrixProduitsEnStock(new BigDecimal("1.05")));
    }
}

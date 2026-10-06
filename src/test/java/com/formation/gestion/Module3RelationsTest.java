package com.formation.gestion;

import com.formation.gestion.entity.Commande;
import com.formation.gestion.service.Tp3Service;
import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class Module3RelationsTest extends BaseTest {

    @Autowired
    Tp3Service service;

    @Test
    void cascadeOrphanRemovalLazyEtCleEtrangere() {
        Tp3Service.Resultat r = service.creerJeuDeDonnees();
        assertEquals(2, service.nombreDeLignes(r.commandeId()), "persist en cascade des lignes");

        service.retirerPremiereLigne(r.commandeId());
        assertEquals(1, service.nombreDeLignes(r.commandeId()), "orphanRemoval");

        Commande c = service.trouver(r.commandeId());
        assertThrows(LazyInitializationException.class, () -> c.getLignes().size());

        assertThrows(RuntimeException.class, () -> service.supprimerClient(r.clientId()),
                "la base refuse de supprimer un client qui a des commandes");
    }
}

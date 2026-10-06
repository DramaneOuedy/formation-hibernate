package com.formation.gestion;

import com.formation.gestion.entity.Utilisateur;
import com.formation.gestion.service.UtilisateurService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Module1CycleDeVieTest extends BaseTest {

    @Autowired
    UtilisateurService service;

    @Test
    void dirtyCheckingMergeEtCacheDePremierNiveau() {
        Long id = service.creer("Kone Awa", "awa.kone@exemple.ci");

        service.changerEmail(id, "a.kone@exemple.ci");
        assertEquals("a.kone@exemple.ci", service.trouver(id).getEmail());

        Utilisateur detache = service.trouver(id);
        detache.setNom("Modifie hors transaction");
        assertEquals("Kone Awa", service.trouver(id).getNom(), "un objet detache n'est pas synchronise");

        Utilisateur gere = service.enregistrer(detache);
        assertNotSame(detache, gere, "merge renvoie une autre instance");
        assertEquals("Modifie hors transaction", service.trouver(id).getNom());

        assertTrue(service.deuxFindMemeInstance(id));

        service.supprimer(id);
        assertNull(service.trouver(id));
    }
}

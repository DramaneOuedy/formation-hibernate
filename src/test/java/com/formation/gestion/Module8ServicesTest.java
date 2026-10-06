package com.formation.gestion;

import com.formation.gestion.dto.ClientDto;
import com.formation.gestion.entity.Adresse;
import com.formation.gestion.entity.StatutClient;
import com.formation.gestion.service.ClientService;
import com.formation.gestion.service.JeuDeDonneesService;
import com.formation.gestion.service.MetierException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class Module8ServicesTest extends BaseTest {

    @Autowired
    JeuDeDonneesService jeu;

    @Autowired
    ClientService service;

    @Test
    void serviceSpringDataEtDto() {
        JeuDeDonneesService.Catalogue cat = jeu.creerClientsEtCatalogue();
        jeu.creerCommandes(6);

        ClientDto cree = service.creer("Bamba Ali", "a.bamba@exemple.ci", new Adresse("Rue 1", "Abidjan", "CI"));
        assertEquals("PROSPECT", cree.statut());
        assertThrows(MetierException.class, () -> service.creer("Doublon", "a.bamba@exemple.ci", null));

        assertEquals("+225 01", service.modifier(cat.clientId(), "Traore M.", "+225 01").telephone());

        assertEquals(2, service.parStatut(StatutClient.ACTIF, 0, 10).getTotalElements());
        assertEquals(2L, service.detailAvecCommandes(cat.clientId()).nbCommandes());
        assertEquals(4, service.nombreDeCommandesParClient().size());
        assertEquals(4, service.desactiverInscritsAvant(LocalDate.now().plusDays(1)));
    }
}

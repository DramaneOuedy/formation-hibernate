package com.formation.gestion;

import com.formation.gestion.service.JeuDeDonneesService;
import com.formation.gestion.service.RapportService;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Module6PerformanceTest extends BaseTest {

    @Autowired
    JeuDeDonneesService jeu;

    @Autowired
    RapportService rapport;

    @Test
    void leNPlus1EtSesCorrections() {
        int nbClients = 20;
        jeu.genererClientsAvecCommandes(nbClients, 3);

        Statistics stats = statistiques();
        assertEquals(nbClients, rapport.rapportClientsNPlus1().size());
        assertEquals(1 + nbClients, stats.getPrepareStatementCount(), "1 requete + 1 par client");

        stats = statistiques();
        rapport.rapportClientsJoinFetch();
        assertEquals(1, stats.getPrepareStatementCount(), "JOIN FETCH");

        stats = statistiques();
        rapport.rapportClientsEntityGraph();
        assertEquals(1, stats.getPrepareStatementCount(), "Entity Graph");

        stats = statistiques();
        assertEquals(3L, rapport.rapportClientsDto().get(0).nbCommandes());
        assertEquals(1, stats.getPrepareStatementCount(), "Projection DTO");
    }
}

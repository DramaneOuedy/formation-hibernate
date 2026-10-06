package com.formation.gestion;

import com.formation.gestion.dto.ClientCaDto;
import com.formation.gestion.entity.Adresse;
import com.formation.gestion.service.ClientService;
import com.formation.gestion.service.CommandeService;
import com.formation.gestion.service.JeuDeDonneesService;
import com.formation.gestion.service.ProduitService;
import com.formation.gestion.service.RapportService;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Module9MiniProjetTest extends BaseTest {

    @Autowired ClientService clients;
    @Autowired ProduitService produits;
    @Autowired CommandeService commandes;
    @Autowired RapportService rapport;
    @Autowired JeuDeDonneesService jeu;

    @Test
    void scenarioComplet() {
        Long clientId = clients.creer("Ouattara Salif", "s.ouattara@exemple.ci",
                new Adresse("Rue du Plateau", "Abidjan", "CI")).id();
        Long cafe = produits.creer("CAF-1", "Cafe 1 kg", new BigDecimal("6500"), 30);
        Long the = produits.creer("THE-1", "The vert", new BigDecimal("3000"), 20);

        Long commandeId = commandes.creer(clientId);
        commandes.ajouterProduit(commandeId, cafe, 2);
        commandes.ajouterProduit(commandeId, the, 3);

        assertEquals(0, new BigDecimal("22000").compareTo(commandes.montantTotal(commandeId)));
        assertEquals(1, commandes.commandesDuClient(clientId).size());
        assertEquals(28, commandes.stock(cafe));

        jeu.genererClientsAvecCommandes(10, 2);
        assertEquals(21, commandes.lister(0, 5).getTotalElements());
        assertEquals(5, commandes.lister(0, 5).getContent().size());

        List<ClientCaDto> naif = rapport.chiffreAffairesParClientNaif();
        Statistics stats = statistiques();
        List<ClientCaDto> optimise = rapport.chiffreAffairesParClient();
        assertEquals(1, stats.getPrepareStatementCount());
        assertEquals(naif.size(), optimise.size());
        for (int i = 0; i < naif.size(); i++) {
            assertEquals(naif.get(i).nbCommandes(), optimise.get(i).nbCommandes());
            assertEquals(0, naif.get(i).chiffreAffaires().compareTo(optimise.get(i).chiffreAffaires()));
        }
    }
}

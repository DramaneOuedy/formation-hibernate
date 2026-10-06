package com.formation.gestion.tp;

import com.formation.gestion.dto.ClientCaDto;
import com.formation.gestion.entity.Adresse;
import com.formation.gestion.entity.StatutClient;
import com.formation.gestion.service.ClientService;
import com.formation.gestion.service.CommandeService;
import com.formation.gestion.service.JeuDeDonneesService;
import com.formation.gestion.service.ProduitService;
import com.formation.gestion.service.RapportService;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Module 9 - mini-projet : mvn spring-boot:run -Dspring-boot.run.profiles=projet
 * Deroule les 11 fonctionnalites demandees.
 */
@Component
@Profile("projet")
public class MiniProjetRunner implements CommandLineRunner {

    @PersistenceUnit
    private EntityManagerFactory emf;

    private final ClientService clients;
    private final ProduitService produits;
    private final CommandeService commandes;
    private final RapportService rapport;
    private final JeuDeDonneesService jeu;

    public MiniProjetRunner(ClientService clients, ProduitService produits, CommandeService commandes,
                            RapportService rapport, JeuDeDonneesService jeu) {
        this.clients = clients;
        this.produits = produits;
        this.commandes = commandes;
        this.rapport = rapport;
        this.jeu = jeu;
    }

    @Override
    public void run(String... args) {
        etape("1. Creer un client");
        Long clientId = clients.creer("Ouattara Salif", "s.ouattara@exemple.ci",
                new Adresse("Rue du Plateau", "Abidjan", "Cote d'Ivoire")).id();

        etape("2. Modifier un client");
        System.out.println(clients.modifier(clientId, "Ouattara Salif", "+225 05 11 22 33"));

        etape("3. Rechercher des clients (ville = Abidjan)");
        clients.rechercher(null, "Abidjan", null).forEach(System.out::println);

        etape("4. Creer des produits");
        Long cafe = produits.creer("CAF-1", "Cafe 1 kg", new BigDecimal("6500"), 30);
        Long the = produits.creer("THE-1", "The vert 500 g", new BigDecimal("3000"), 20);

        etape("5 et 6. Creer une commande et y ajouter des produits");
        Long commandeId = commandes.creer(clientId);
        commandes.ajouterProduit(commandeId, cafe, 2);
        commandes.ajouterProduit(commandeId, the, 3);

        etape("7. Montant total");
        System.out.println(commandes.montantTotal(commandeId) + " FCFA");   // 2 x 6500 + 3 x 3000 = 22000

        etape("8. Commandes du client (DTO)");
        commandes.commandesDuClient(clientId).forEach(System.out::println);

        etape("9. Pagination (page 0, taille 5)");
        jeu.creerClientsEtCatalogue();
        jeu.creerCommandes(12);
        var page = commandes.lister(0, 5);
        System.out.println(page.getTotalElements() + " commandes, " + page.getTotalPages() + " pages");
        page.getContent().forEach(System.out::println);

        etape("10. Transaction complete avec rollback si stock insuffisant");
        Map<Long, Integer> demande = new LinkedHashMap<>();
        demande.put(cafe, 1);
        demande.put(the, 999);
        try {
            commandes.passerCommande(clientId, demande);
        } catch (IllegalStateException e) {
            System.out.println("Rollback : " + e.getMessage());
        }
        System.out.println("Stock cafe inchange (28 attendu) : " + commandes.stock(cafe));

        etape("11. Optimiser le rapport chiffre d'affaires par client");
        jeu.genererClientsAvecCommandes(50, 3);
        Statistics stats = emf.unwrap(SessionFactory.class).getStatistics();
        stats.clear();
        List<ClientCaDto> naif = rapport.chiffreAffairesParClientNaif();
        long requetesNaif = stats.getPrepareStatementCount();
        stats.clear();
        List<ClientCaDto> optimise = rapport.chiffreAffairesParClient();
        System.out.println("Version naive : " + requetesNaif + " requetes | version DTO : "
                + stats.getPrepareStatementCount() + " requete(s) | " + optimise.size() + " clients"
                + " | memes resultats : " + memesResultats(naif, optimise));
        System.out.println("Clients ACTIF : " + clients.parStatut(StatutClient.ACTIF, 0, 100).getTotalElements());
    }

    private static boolean memesResultats(List<ClientCaDto> a, List<ClientCaDto> b) {
        if (a.size() != b.size()) {
            return false;
        }
        for (int i = 0; i < a.size(); i++) {
            if (!a.get(i).nom().equals(b.get(i).nom())
                    || !a.get(i).nbCommandes().equals(b.get(i).nbCommandes())
                    || a.get(i).chiffreAffaires().compareTo(b.get(i).chiffreAffaires()) != 0) {
                return false;
            }
        }
        return true;
    }

    private static void etape(String titre) {
        System.out.println("\n=== MINI-PROJET : " + titre + " ===");
    }
}

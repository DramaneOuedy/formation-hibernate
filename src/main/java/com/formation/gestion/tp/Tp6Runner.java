package com.formation.gestion.tp;

import com.formation.gestion.dto.ClientNbCommandes;
import com.formation.gestion.service.ImportService;
import com.formation.gestion.service.JeuDeDonneesService;
import com.formation.gestion.service.RapportService;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Supplier;

/**
 * TP 6 - mvn spring-boot:run -Dspring-boot.run.profiles=tp6
 * Cas pratique : "1 requete Client + 100 requetes Commande".
 */
@Component
@Profile("tp6")
public class Tp6Runner implements CommandLineRunner {

    @PersistenceUnit
    private EntityManagerFactory emf;

    private final JeuDeDonneesService jeu;
    private final RapportService rapport;
    private final ImportService importService;

    public Tp6Runner(JeuDeDonneesService jeu, RapportService rapport, ImportService importService) {
        this.jeu = jeu;
        this.rapport = rapport;
        this.importService = importService;
    }

    @Override
    public void run(String... args) {
        System.out.println("\n=== TP6 : generation de 100 clients x 3 commandes ===");
        jeu.genererClientsAvecCommandes(100, 3);

        Statistics stats = emf.unwrap(SessionFactory.class).getStatistics();

        mesurer(stats, "N+1 (version naive)", rapport::rapportClientsNPlus1);
        mesurer(stats, "LEFT JOIN FETCH", rapport::rapportClientsJoinFetch);
        mesurer(stats, "Entity Graph", rapport::rapportClientsEntityGraph);
        mesurer(stats, "Projection DTO", rapport::rapportClientsDto);

        System.out.println("\n=== TP6 : import de 1 000 produits (flush + clear tous les 50) ===");
        stats.clear();
        long debut = System.currentTimeMillis();
        importService.importerProduits(1_000);
        System.out.println("Duree : " + (System.currentTimeMillis() - debut) + " ms, requetes : "
                + stats.getPrepareStatementCount()
                + " (IDENTITY : chaque INSERT part seul, pas de batching des INSERT)");
    }

    private void mesurer(Statistics stats, String libelle, Supplier<List<ClientNbCommandes>> traitement) {
        stats.clear();
        List<ClientNbCommandes> lignes = traitement.get();
        System.out.printf("%n>>> %-22s : %4d requetes SQL pour %d clients%n",
                libelle, stats.getPrepareStatementCount(), lignes.size());
    }
}

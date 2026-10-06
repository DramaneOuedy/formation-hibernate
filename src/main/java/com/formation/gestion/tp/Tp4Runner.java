package com.formation.gestion.tp;

import com.formation.gestion.entity.Commande;
import com.formation.gestion.entity.StatutClient;
import com.formation.gestion.service.JeuDeDonneesService;
import com.formation.gestion.service.Tp4Service;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/** TP 4 - mvn spring-boot:run -Dspring-boot.run.profiles=tp4 */
@Component
@Profile("tp4")
public class Tp4Runner implements CommandLineRunner {

    private final JeuDeDonneesService jeu;
    private final Tp4Service service;

    public Tp4Runner(JeuDeDonneesService jeu, Tp4Service service) {
        this.jeu = jeu;
        this.service = service;
    }

    @Override
    public void run(String... args) {
        JeuDeDonneesService.Catalogue cat = jeu.creerClientsEtCatalogue();
        jeu.creerCommandes(12);

        titre("1. Client par email");
        service.clientParEmail("m.traore@exemple.ci")
                .ifPresent(c -> System.out.println(c.getNom() + " (" + c.getStatut() + ")"));

        titre("2. Commandes du client");
        service.commandesDuClient(cat.clientId())
                .forEach(c -> System.out.println(c.getReference() + " du " + c.getDateCommande().toLocalDate()));

        titre("3. Commandes des 15 derniers jours");
        service.commandesEntre(LocalDateTime.now().minusDays(15), LocalDateTime.now())
                .forEach(c -> System.out.println(c.getReference()));

        titre("4. Commandes >= 50 000 (DTO)");
        service.commandesAuDessusDe(new BigDecimal("50000")).forEach(System.out::println);

        titre("5. Pagination : page 1 (taille 5)");
        System.out.println("Total : " + service.compterCommandes());
        service.page(1, 5).forEach(c -> System.out.println(c.getReference()));

        titre("6. Detail complet en une requete");
        Commande detail = service.detailComplet(service.page(0, 1).get(0).getId()).orElseThrow();
        // Transaction terminee, mais les lignes ont ete chargees par le JOIN FETCH : pas d'exception
        System.out.println(detail.getReference() + " - " + detail.getClient().getNom());
        detail.getLignes().forEach(l ->
                System.out.println("  " + l.getProduit().getNom() + " x" + l.getQuantite() + " = " + l.getSousTotal()));

        titre("Recherche multicritere : ville = Abidjan, statut ACTIF");
        service.rechercherClients(null, "Abidjan", StatutClient.ACTIF).forEach(c -> System.out.println(c.getNom()));

        titre("UPDATE en masse : +5 % sur les produits en stock");
        System.out.println(service.augmenterPrixProduitsEnStock(new BigDecimal("1.05")) + " produits modifies");

        titre("SQL natif : produits avec moins de 45 en stock");
        List<String> ruptures = service.produitsEnRupture(45).stream().map(p -> p.getCode()).toList();
        System.out.println(ruptures);
    }

    private static void titre(String t) {
        System.out.println("\n=== TP4 : " + t + " ===");
    }
}

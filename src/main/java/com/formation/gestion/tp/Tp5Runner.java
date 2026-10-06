package com.formation.gestion.tp;

import com.formation.gestion.service.CommandeService;
import com.formation.gestion.service.JeuDeDonneesService;
import com.formation.gestion.service.StockInsuffisantException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

/** TP 5 - mvn spring-boot:run -Dspring-boot.run.profiles=tp5 */
@Component
@Profile("tp5")
public class Tp5Runner implements CommandLineRunner {

    private final JeuDeDonneesService jeu;
    private final CommandeService service;

    public Tp5Runner(JeuDeDonneesService jeu, CommandeService service) {
        this.jeu = jeu;
        this.service = service;
    }

    @Override
    public void run(String... args) {
        JeuDeDonneesService.Catalogue cat = jeu.creerClientsEtCatalogue();
        Long rizId = cat.rizId();
        Long huileId = cat.huileId();

        System.out.println("\n=== TP5 : commande valide -> commit ===");
        Map<Long, Integer> ok = new LinkedHashMap<>();
        ok.put(rizId, 2);
        ok.put(huileId, 1);
        service.passerCommande(cat.clientId(), ok);
        int stockRiz = service.stock(rizId);
        long nbCommandes = service.nombreDeCommandes();
        System.out.println("Stock riz = " + stockRiz + ", commandes = " + nbCommandes);

        System.out.println("\n=== TP5 : stock insuffisant -> rollback ===");
        Map<Long, Integer> ko = new LinkedHashMap<>();
        ko.put(rizId, 1);            // stock decremente en memoire...
        ko.put(huileId, 9_999);      // ...puis exception
        try {
            service.passerCommande(cat.clientId(), ko);
        } catch (IllegalStateException e) {
            System.out.println("Rollback : " + e.getMessage());
        }
        System.out.println("Stock riz inchange : " + (service.stock(rizId) == stockRiz));
        System.out.println("Aucune commande ajoutee : " + (service.nombreDeCommandes() == nbCommandes));

        System.out.println("\n=== TP5 variante : exception controlee SANS rollbackFor ===");
        try {
            service.passerCommandeSansRollback(cat.clientId(), ko);
        } catch (StockInsuffisantException e) {
            System.out.println("Exception : " + e.getMessage());
        }
        System.out.println("Stock riz = " + service.stock(rizId) + " (avant : " + stockRiz
                + ") -> la transaction a ete VALIDEE malgre l'exception !");
        int stockApresPiege = service.stock(rizId);

        System.out.println("\n=== TP5 variante : AVEC rollbackFor ===");
        try {
            service.passerCommandeAvecRollback(cat.clientId(), ko);
        } catch (StockInsuffisantException e) {
            System.out.println("Exception : " + e.getMessage());
        }
        System.out.println("Stock riz inchange cette fois : " + (service.stock(rizId) == stockApresPiege));
    }
}

package com.formation.gestion.tp;

import com.formation.gestion.entity.Commande;
import com.formation.gestion.service.Tp3Service;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/** TP 3 - mvn spring-boot:run -Dspring-boot.run.profiles=tp3 */
@Component
@Profile("tp3")
public class Tp3Runner implements CommandLineRunner {

    private final Tp3Service service;

    public Tp3Runner(Tp3Service service) {
        this.service = service;
    }

    @Override
    public void run(String... args) {
        System.out.println("\n=== TP3 : persist en cascade ===");
        Tp3Service.Resultat r = service.creerJeuDeDonnees();

        System.out.println("\n=== TP3 : orphanRemoval ===");
        service.retirerPremiereLigne(r.commandeId());
        System.out.println("Lignes restantes : " + service.nombreDeLignes(r.commandeId()));

        System.out.println("\n=== TP3 : LazyInitializationException ===");
        Commande c = service.trouver(r.commandeId());   // transaction terminee
        try {
            c.getLignes().size();
        } catch (RuntimeException e) {
            System.out.println("Exception attendue : " + e.getClass().getSimpleName());
        }

        System.out.println("\n=== TP3 : suppression d'un client qui a des commandes ===");
        try {
            service.supprimerClient(r.clientId());
        } catch (RuntimeException e) {
            System.out.println("Refuse par la base : " + e.getClass().getSimpleName()
                    + " -> on desactive un client (statut INACTIF), on ne le supprime pas.");
        }
    }
}

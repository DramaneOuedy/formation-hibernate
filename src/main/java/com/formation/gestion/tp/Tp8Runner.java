package com.formation.gestion.tp;

import com.formation.gestion.entity.Adresse;
import com.formation.gestion.entity.StatutClient;
import com.formation.gestion.service.ClientService;
import com.formation.gestion.service.JeuDeDonneesService;
import com.formation.gestion.service.MetierException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/** TP 8 - mvn spring-boot:run -Dspring-boot.run.profiles=tp8 */
@Component
@Profile("tp8")
public class Tp8Runner implements CommandLineRunner {

    private final JeuDeDonneesService jeu;
    private final ClientService service;

    public Tp8Runner(JeuDeDonneesService jeu, ClientService service) {
        this.jeu = jeu;
        this.service = service;
    }

    @Override
    public void run(String... args) {
        JeuDeDonneesService.Catalogue cat = jeu.creerClientsEtCatalogue();
        jeu.creerCommandes(6);

        System.out.println("\n=== TP8 : creation via le service (DTO en sortie) ===");
        System.out.println(service.creer("Bamba Ali", "a.bamba@exemple.ci",
                new Adresse("Avenue Chardy", "Abidjan", "Cote d'Ivoire")));

        System.out.println("\n=== TP8 : regle metier email unique ===");
        try {
            service.creer("Doublon", "a.bamba@exemple.ci", null);
        } catch (MetierException e) {
            System.out.println("MetierException : " + e.getMessage());
        }

        System.out.println("\n=== TP8 : modification par dirty checking ===");
        System.out.println(service.modifier(cat.clientId(), "Traore Moussa Ibrahim", "+225 07 00 00 00"));

        System.out.println("\n=== TP8 : pagination Spring Data (clients ACTIF, page 0, taille 2) ===");
        var page = service.parStatut(StatutClient.ACTIF, 0, 2);
        System.out.println(page.getTotalElements() + " clients, " + page.getTotalPages() + " pages");
        page.getContent().forEach(System.out::println);

        System.out.println("\n=== TP8 : @EntityGraph (client + commandes en une requete) ===");
        System.out.println(service.detailAvecCommandes(cat.clientId()));

        System.out.println("\n=== TP8 : @Query avec projection DTO ===");
        service.nombreDeCommandesParClient().forEach(System.out::println);

        System.out.println("\n=== TP8 : @Modifying (UPDATE en masse) ===");
        System.out.println(service.desactiverInscritsAvant(LocalDate.now().plusDays(1)) + " clients desactives");
    }
}

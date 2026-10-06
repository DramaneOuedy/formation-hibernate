package com.formation.gestion.tp;

import com.formation.gestion.entity.Utilisateur;
import com.formation.gestion.service.UtilisateurService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * TP 1 - mvn spring-boot:run -Dspring-boot.run.profiles=tp1
 * Observer le SQL emis a chaque etape.
 */
@Component
@Profile("tp1")
public class Tp1Runner implements CommandLineRunner {

    private final UtilisateurService service;

    public Tp1Runner(UtilisateurService service) {
        this.service = service;
    }

    @Override
    public void run(String... args) {
        System.out.println("\n=== TP1 : creation ===");
        Long id = service.creer("Kone Awa", "awa.kone@exemple.ci");          // INSERT

        System.out.println("\n=== TP1 : modification par dirty checking ===");
        service.changerEmail(id, "a.kone@exemple.ci");                       // SELECT + UPDATE

        System.out.println("\n=== TP1 : objet detache puis merge ===");
        Utilisateur detache = service.trouver(id);
        detache.setNom("Kone Awa Mariam");      // hors transaction : aucun SQL
        Utilisateur gere = service.enregistrer(detache);                    // SELECT + UPDATE
        System.out.println("merge renvoie une autre instance : " + (gere != detache));

        System.out.println("\n=== TP1 : cache de premier niveau ===");
        System.out.println("Meme instance dans une transaction : " + service.deuxFindMemeInstance(id));

        System.out.println("\n=== TP1 : suppression ===");
        service.supprimer(id);                                               // SELECT + DELETE
    }
}

package com.formation.gestion.tp;

import com.formation.gestion.entity.Adresse;
import com.formation.gestion.service.Tp2Service;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * TP 2 - mvn spring-boot:run -Dspring-boot.run.profiles=tp2
 * Verifier ensuite dans MySQL : SHOW CREATE TABLE client;
 */
@Component
@Profile("tp2")
public class Tp2Runner implements CommandLineRunner {

    private final Tp2Service service;

    public Tp2Runner(Tp2Service service) {
        this.service = service;
    }

    @Override
    public void run(String... args) {
        Adresse abidjan = new Adresse("Boulevard Latrille", "Abidjan", "Cote d'Ivoire");
        Long id = service.creerClient("Yao Kouassi", "yao.kouassi@exemple.ci", abidjan);
        System.out.println("\nClient cree avec l'id " + id);

        System.out.println("\n=== TP2 : second client avec le meme email ===");
        try {
            service.creerClient("Autre Client", "yao.kouassi@exemple.ci", abidjan);
        } catch (RuntimeException e) {
            System.out.println("Exception attendue : " + e.getClass().getName());
            Throwable cause = e;
            while (cause.getCause() != null) {
                cause = cause.getCause();
            }
            System.out.println("Cause SQL : " + cause.getMessage());
        }
    }
}

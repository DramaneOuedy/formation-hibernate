package com.formation.gestion.tp;

import com.formation.gestion.service.CategorieService;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/** TP 7 - mvn spring-boot:run -Dspring-boot.run.profiles=tp7 */
@Component
@Profile("tp7")
public class Tp7Runner implements CommandLineRunner {

    @PersistenceUnit
    private EntityManagerFactory emf;

    private final CategorieService service;

    public Tp7Runner(CategorieService service) {
        this.service = service;
    }

    @Override
    public void run(String... args) {
        Long id = service.creer("Alimentaire");
        service.creer("Hygiene");
        service.creer("Boissons");

        // Le persist a deja rempli le cache : on le vide pour partir d'un cache froid
        emf.getCache().evictAll();
        Statistics stats = emf.unwrap(SessionFactory.class).getStatistics();
        stats.clear();

        System.out.println("\n=== TP7 : meme categorie lue dans deux transactions (attendu : 1 SELECT, 1 hit) ===");
        service.nom(id);
        service.nom(id);
        System.out.println("Requetes SQL : " + stats.getPrepareStatementCount()
                + " | hits cache L2 : " + stats.getSecondLevelCacheHitCount()
                + " | puts cache L2 : " + stats.getSecondLevelCachePutCount());

        System.out.println("\n=== TP7 : cache de requete ===");
        stats.clear();
        service.lister();
        service.lister();
        System.out.println("Requetes SQL : " + stats.getPrepareStatementCount()
                + " | hits cache de requete : " + stats.getQueryCacheHitCount());
    }
}

package com.formation.gestion;

import com.formation.gestion.service.CategorieService;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Module7CacheTest extends BaseTest {

    @Autowired
    CategorieService service;

    @Test
    void secondeLectureServieParLeCacheDeuxiemeNiveau() {
        Long id = service.creer("Alimentaire");
        emf.getCache().evictAll();

        Statistics stats = statistiques();
        service.nom(id);
        service.nom(id);
        assertEquals(1, stats.getPrepareStatementCount(), "un seul SELECT");
        assertEquals(1, stats.getSecondLevelCacheHitCount(), "la 2e lecture vient du cache");
    }
}

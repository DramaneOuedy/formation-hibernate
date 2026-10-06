package com.formation.gestion.service;

import com.formation.gestion.entity.Produit;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/** Module 6 : ecritures en masse avec flush() + clear() reguliers. */
@Service
public class ImportService {

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public void importerProduits(int nombre) {
        for (int i = 1; i <= nombre; i++) {
            em.persist(new Produit("P-" + i, "Produit " + i, BigDecimal.TEN, 100));
            if (i % 50 == 0) {      // vider regulierement le contexte
                em.flush();
                em.clear();
            }
        }
    }
}

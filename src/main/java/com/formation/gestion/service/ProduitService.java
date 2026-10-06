package com.formation.gestion.service;

import com.formation.gestion.entity.Produit;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/** Module 9 : creation de produits. */
@Service
public class ProduitService {

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public Long creer(String code, String nom, BigDecimal prix, int stock) {
        if (prix.signum() <= 0) {
            throw new MetierException("Le prix doit etre positif");
        }
        Produit p = new Produit(code, nom, prix, stock);
        em.persist(p);
        return p.getId();
    }
}

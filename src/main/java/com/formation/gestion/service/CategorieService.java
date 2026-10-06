package com.formation.gestion.service;

import com.formation.gestion.entity.Categorie;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Module 7 : lectures servies par le cache de 2e niveau. */
@Service
public class CategorieService {

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public Long creer(String nom) {
        Categorie c = new Categorie(nom);
        em.persist(c);
        return c.getId();
    }

    /** Chaque appel = une transaction = un nouveau contexte de persistance (cache L1 vide). */
    @Transactional(readOnly = true)
    public String nom(Long id) {
        return em.find(Categorie.class, id).getNom();
    }

    /** Cache de requete : a activer requete par requete. */
    @Transactional(readOnly = true)
    public List<String> lister() {
        return em.createQuery("select c from Categorie c order by c.nom", Categorie.class)
                .setHint("org.hibernate.cacheable", true)
                .getResultList().stream()
                .map(Categorie::getNom)
                .toList();
    }
}

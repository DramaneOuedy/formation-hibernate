package com.formation.gestion.service;

import com.formation.gestion.entity.Utilisateur;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Module 1 : cycle de vie d'une entite (persist, find, dirty checking, merge, remove). */
@Service
public class UtilisateurService {

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public Long creer(String nom, String email) {
        Utilisateur u = new Utilisateur(nom, email);
        em.persist(u);                          // Transient -> Managed
        return u.getId();
    }

    @Transactional(readOnly = true)
    public Utilisateur trouver(Long id) {
        return em.find(Utilisateur.class, id);  // detache des la fin de la methode
    }

    @Transactional
    public void changerEmail(Long id, String email) {
        Utilisateur u = em.find(Utilisateur.class, id);
        u.setEmail(email);                      // dirty checking : aucun appel a update()
    }

    @Transactional
    public Utilisateur enregistrer(Utilisateur detache) {
        return em.merge(detache);               // renvoie la copie geree
    }

    @Transactional
    public void supprimer(Long id) {
        em.remove(em.find(Utilisateur.class, id));
    }

    /** Question de debrief 2 : deux find dans la meme transaction = un seul SELECT. */
    @Transactional(readOnly = true)
    public boolean deuxFindMemeInstance(Long id) {
        Utilisateur a = em.find(Utilisateur.class, id);
        Utilisateur b = em.find(Utilisateur.class, id);   // servi par le cache de 1er niveau
        return a == b;
    }
}

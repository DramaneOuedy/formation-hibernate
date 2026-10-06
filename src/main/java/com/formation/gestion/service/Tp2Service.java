package com.formation.gestion.service;

import com.formation.gestion.entity.Adresse;
import com.formation.gestion.entity.Client;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Module 2 : enregistrement d'un client (mapping avance). */
@Service
public class Tp2Service {

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public Long creerClient(String nom, String email, Adresse adresse) {
        Client client = new Client(nom, email, adresse);
        em.persist(client);   // IDENTITY : l'INSERT part immediatement
        return client.getId();
    }
}

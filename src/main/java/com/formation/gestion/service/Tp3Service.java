package com.formation.gestion.service;

import com.formation.gestion.entity.Adresse;
import com.formation.gestion.entity.Categorie;
import com.formation.gestion.entity.Client;
import com.formation.gestion.entity.Commande;
import com.formation.gestion.entity.Produit;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/** Module 3 : relations, cascades, orphanRemoval, LAZY. */
@Service
public class Tp3Service {

    public record Resultat(Long clientId, Long commandeId) {
    }

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public Resultat creerJeuDeDonnees() {
        Client client = new Client("Traore Moussa", "m.traore@exemple.ci",
                new Adresse("Rue des Jardins", "Abidjan", "Cote d'Ivoire"));
        Produit riz = new Produit("RIZ-25", "Riz 25 kg", new BigDecimal("18500"), 100);
        Produit huile = new Produit("HUI-5", "Huile 5 L", new BigDecimal("7500"), 50);
        Categorie alimentaire = new Categorie("Alimentaire");
        riz.ajouterCategorie(alimentaire);
        huile.ajouterCategorie(alimentaire);

        em.persist(client);
        em.persist(alimentaire);
        em.persist(riz);
        em.persist(huile);

        Commande cmd = new Commande("CMD-0001", client);
        cmd.ajouterLigne(riz, 2);
        cmd.ajouterLigne(huile, 3);
        em.persist(cmd);                         // 1 INSERT commande + 2 INSERT lignes (cascade)
        return new Resultat(client.getId(), cmd.getId());
    }

    @Transactional
    public void retirerPremiereLigne(Long commandeId) {
        Commande cmd = em.find(Commande.class, commandeId);
        cmd.retirerLigne(cmd.getLignes().get(0)); // DELETE ligne_commande au commit
    }

    @Transactional(readOnly = true)
    public Commande trouver(Long commandeId) {
        return em.find(Commande.class, commandeId);
    }

    @Transactional(readOnly = true)
    public int nombreDeLignes(Long commandeId) {
        return em.find(Commande.class, commandeId).getLignes().size();
    }

    @Transactional
    public void supprimerClient(Long clientId) {
        em.remove(em.find(Client.class, clientId));  // echoue au flush : FK depuis commande
    }
}

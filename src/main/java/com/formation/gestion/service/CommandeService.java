package com.formation.gestion.service;

import com.formation.gestion.entity.Client;
import com.formation.gestion.entity.Commande;
import com.formation.gestion.entity.Produit;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

/** Module 5 : transactions avec @Transactional. */
@Service
public class CommandeService {

    @PersistenceContext
    private EntityManager em;

    /** Tout ou rien : commit si tout passe, rollback sur RuntimeException. */
    @Transactional
    public Long passerCommande(Long clientId, Map<Long, Integer> quantitesParProduit) {
        Client client = em.find(Client.class, clientId);
        if (client == null) {
            throw new IllegalArgumentException("Client introuvable : " + clientId);
        }
        Commande commande = new Commande(nouvelleReference(), client);
        quantitesParProduit.forEach((produitId, quantite) -> {
            Produit produit = em.find(Produit.class, produitId);
            produit.retirerStock(quantite);          // peut lever IllegalStateException
            commande.ajouterLigne(produit, quantite);
        });
        em.persist(commande);                         // cascade sur les lignes
        return commande.getId();
    }

    /**
     * Variante PIEGE : exception controlee sans rollbackFor.
     * La transaction est VALIDEE : le stock deja decremente est enregistre alors qu'aucune commande n'existe.
     */
    @Transactional
    public Long passerCommandeSansRollback(Long clientId, Map<Long, Integer> quantitesParProduit)
            throws StockInsuffisantException {
        return passerAvecExceptionControlee(clientId, quantitesParProduit);
    }

    /** Variante CORRIGEE : rollbackFor sur l'exception controlee. */
    @Transactional(rollbackFor = StockInsuffisantException.class)
    public Long passerCommandeAvecRollback(Long clientId, Map<Long, Integer> quantitesParProduit)
            throws StockInsuffisantException {
        return passerAvecExceptionControlee(clientId, quantitesParProduit);
    }

    @Transactional(readOnly = true)
    public int stock(Long produitId) {
        return em.find(Produit.class, produitId).getStock();
    }

    @Transactional(readOnly = true)
    public long nombreDeCommandes() {
        return em.createQuery("select count(c) from Commande c", Long.class).getSingleResult();
    }

    // Methode privee : appelee depuis une methode @Transactional, elle s'execute dans SA transaction
    private Long passerAvecExceptionControlee(Long clientId, Map<Long, Integer> quantitesParProduit)
            throws StockInsuffisantException {
        Client client = em.find(Client.class, clientId);
        Commande commande = new Commande(nouvelleReference(), client);
        for (Map.Entry<Long, Integer> e : quantitesParProduit.entrySet()) {   // boucle for : un lambda
            Produit produit = em.find(Produit.class, e.getKey());             // ne peut pas lever
            if (e.getValue() > produit.getStock()) {                          // d'exception controlee
                throw new StockInsuffisantException("Stock insuffisant pour " + produit.getCode());
            }
            produit.retirerStock(e.getValue());
            commande.ajouterLigne(produit, e.getValue());
        }
        em.persist(commande);
        return commande.getId();
    }

    private static String nouvelleReference() {
        return "CMD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}

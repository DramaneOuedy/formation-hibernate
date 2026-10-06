package com.formation.gestion.service;

import com.formation.gestion.dto.CommandeResumeDto;
import com.formation.gestion.entity.Client;
import com.formation.gestion.entity.Commande;
import com.formation.gestion.entity.Produit;
import com.formation.gestion.entity.StatutCommande;
import com.formation.gestion.repository.CommandeRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** Module 5 : transactions avec @Transactional. Module 9 : cas d'usage du mini-projet. */
@Service
public class CommandeService {

    @PersistenceContext
    private EntityManager em;

    private final CommandeRepository commandeRepository;

    public CommandeService(CommandeRepository commandeRepository) {
        this.commandeRepository = commandeRepository;
    }

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

    // ----- Module 9 : mini-projet -----

    @Transactional
    public Long creer(Long clientId) {
        Client client = em.find(Client.class, clientId);
        if (client == null) {
            throw new MetierException("Client introuvable : " + clientId);
        }
        Commande commande = new Commande(nouvelleReference(), client);
        em.persist(commande);
        return commande.getId();
    }

    @Transactional
    public void ajouterProduit(Long commandeId, Long produitId, int quantite) {
        Commande commande = em.find(Commande.class, commandeId);
        if (commande == null || commande.getStatut() != StatutCommande.BROUILLON) {
            throw new MetierException("Commande introuvable ou deja validee : " + commandeId);
        }
        Produit produit = em.find(Produit.class, produitId);
        produit.retirerStock(quantite);
        commande.ajouterLigne(produit, quantite);   // cascade : INSERT de la ligne au commit
    }

    @Transactional(readOnly = true)
    public BigDecimal montantTotal(Long commandeId) {
        BigDecimal total = em.createQuery("""
                        select sum(l.prixUnitaire * l.quantite)
                        from LigneCommande l where l.commande.id = :id""", BigDecimal.class)
                .setParameter("id", commandeId)
                .getSingleResult();
        return total == null ? BigDecimal.ZERO : total;
    }

    @Transactional(readOnly = true)
    public List<CommandeResumeDto> commandesDuClient(Long clientId) {
        return commandeRepository.resumesDuClient(clientId);
    }

    @Transactional(readOnly = true)
    public Page<CommandeResumeDto> lister(int page, int taille) {
        return commandeRepository.resumes(PageRequest.of(page, taille));
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

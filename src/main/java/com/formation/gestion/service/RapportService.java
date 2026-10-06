package com.formation.gestion.service;

import com.formation.gestion.dto.ClientCaDto;
import com.formation.gestion.dto.ClientNbCommandes;
import com.formation.gestion.entity.Commande;
import com.formation.gestion.entity.Client;
import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Module 6 : le meme rapport ecrit de 4 facons.
 * IMPORTANT : @Transactional(readOnly = true) est obligatoire ici. Sans transaction (et avec
 * open-in-view=false), la boucle leverait une LazyInitializationException au lieu du N+1.
 */
@Service
@Transactional(readOnly = true)
public class RapportService {

    @PersistenceContext
    private EntityManager em;

    /** PROBLEME : 1 requete pour les clients + 1 requete PAR client pour ses commandes. */
    public List<ClientNbCommandes> rapportClientsNPlus1() {
        List<Client> clients = em.createQuery("select c from Client c order by c.id", Client.class)
                .getResultList();
        return clients.stream()
                .map(c -> new ClientNbCommandes(c.getNom(), (long) c.getCommandes().size()))
                .toList();
    }

    /** Solution 1 : LEFT JOIN FETCH (left pour garder les clients sans commande) -> 1 requete. */
    public List<ClientNbCommandes> rapportClientsJoinFetch() {
        return em.createQuery("select c from Client c left join fetch c.commandes order by c.id", Client.class)
                .getResultList().stream()
                .map(c -> new ClientNbCommandes(c.getNom(), (long) c.getCommandes().size()))
                .toList();
    }

    /** Solution 2 : Entity Graph, sans reecrire la requete -> 1 requete. */
    public List<ClientNbCommandes> rapportClientsEntityGraph() {
        EntityGraph<Client> graphe = em.createEntityGraph(Client.class);
        graphe.addAttributeNodes("commandes");
        return em.createQuery("select c from Client c order by c.id", Client.class)
                .setHint("jakarta.persistence.fetchgraph", graphe)
                .getResultList().stream()
                .map(c -> new ClientNbCommandes(c.getNom(), (long) c.getCommandes().size()))
                .toList();
    }

    /** Solution 3 (la meilleure pour un rapport) : projection DTO, aucune entite chargee -> 1 requete. */
    public List<ClientNbCommandes> rapportClientsDto() {
        return em.createQuery("""
                        select new com.formation.gestion.dto.ClientNbCommandes(c.nom, count(o))
                        from Client c left join c.commandes o
                        group by c.id, c.nom
                        order by c.id""", ClientNbCommandes.class)
                .getResultList();
    }

    // ----- Module 9 : fonctionnalite 11 -----

    /** A OPTIMISER : 1 requete clients + 1 par client (commandes) + 1 par commande (lignes). */
    public List<ClientCaDto> chiffreAffairesParClientNaif() {
        return em.createQuery("select c from Client c order by c.id", Client.class)
                .getResultList().stream()
                .map(c -> new ClientCaDto(c.getNom(), (long) c.getCommandes().size(),
                        c.getCommandes().stream()
                                .map(Commande::getMontantTotal)
                                .reduce(BigDecimal.ZERO, BigDecimal::add)))
                .toList();
    }

    /** Version optimisee : une seule requete, aucune entite chargee. */
    public List<ClientCaDto> chiffreAffairesParClient() {
        return em.createQuery("""
                        select new com.formation.gestion.dto.ClientCaDto(
                            c.nom, count(distinct o.id), sum(l.prixUnitaire * l.quantite))
                        from Client c left join c.commandes o left join o.lignes l
                        group by c.id, c.nom
                        order by c.id""", ClientCaDto.class)
                .getResultList();
    }
}

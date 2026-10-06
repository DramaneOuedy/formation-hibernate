package com.formation.gestion.repository;

import com.formation.gestion.dto.CommandeResumeDto;
import com.formation.gestion.entity.Client;
import com.formation.gestion.entity.Commande;
import com.formation.gestion.entity.Produit;
import com.formation.gestion.entity.StatutClient;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Module 4 : requetes JPQL / HQL / SQL natif / Criteria.
 * Un Repository n'ouvre pas de transaction : il est appele depuis un service @Transactional.
 */
@Repository
public class CommandeQueries {

    private static final String RESUME = """
            select new com.formation.gestion.dto.CommandeResumeDto(
                c.id, c.reference, c.dateCommande, cl.nom, sum(l.prixUnitaire * l.quantite))
            from Commande c join c.client cl join c.lignes l
            """;

    @PersistenceContext
    private EntityManager em;

    // 1. Rechercher un client par email
    public Optional<Client> clientParEmail(String email) {
        return em.createQuery("select c from Client c where c.email = :email", Client.class)
                .setParameter("email", email)
                .getResultStream()
                .findFirst();
    }

    // 2. Commandes d'un client
    public List<Commande> commandesDuClient(Long clientId) {
        return em.createQuery("""
                        select c from Commande c
                        where c.client.id = :clientId
                        order by c.dateCommande desc""", Commande.class)
                .setParameter("clientId", clientId)
                .getResultList();
    }

    // 3. Commandes d'une periode
    public List<Commande> commandesEntre(LocalDateTime debut, LocalDateTime fin) {
        return em.createQuery("""
                        select c from Commande c
                        where c.dateCommande between :debut and :fin
                        order by c.dateCommande desc""", Commande.class)
                .setParameter("debut", debut)
                .setParameter("fin", fin)
                .getResultList();
    }

    // 4. Commandes au-dessus d'un montant (GROUP BY + HAVING + projection DTO)
    public List<CommandeResumeDto> commandesAuDessusDe(BigDecimal min) {
        return em.createQuery(RESUME + """
                        group by c.id, c.reference, c.dateCommande, cl.nom
                        having sum(l.prixUnitaire * l.quantite) >= :min
                        order by c.dateCommande desc""", CommandeResumeDto.class)
                .setParameter("min", min)
                .getResultList();
    }

    // 5. Pagination
    public List<Commande> page(int page, int taille) {
        return em.createQuery("select c from Commande c order by c.dateCommande desc, c.id", Commande.class)
                .setFirstResult(page * taille)
                .setMaxResults(taille)
                .getResultList();
    }

    public long compterCommandes() {
        return em.createQuery("select count(c) from Commande c", Long.class).getSingleResult();
    }

    // 6. Detail complet en UNE requete (JOIN FETCH)
    public Optional<Commande> detailComplet(Long id) {
        return em.createQuery("""
                        select c from Commande c
                        join fetch c.client
                        join fetch c.lignes l
                        join fetch l.produit
                        where c.id = :id""", Commande.class)
                .setParameter("id", id)
                .getResultStream()
                .findFirst();
    }

    // Projection DTO sur une periode
    public List<CommandeResumeDto> resumesEntre(LocalDateTime debut, LocalDateTime fin) {
        return em.createQuery(RESUME + """
                        where c.dateCommande between :debut and :fin
                        group by c.id, c.reference, c.dateCommande, cl.nom
                        order by c.dateCommande desc""", CommandeResumeDto.class)
                .setParameter("debut", debut)
                .setParameter("fin", fin)
                .getResultList();
    }

    // Recherche multicritere : criteres optionnels (API Criteria)
    public List<Client> rechercherClients(String nom, String ville, StatutClient statut) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Client> cq = cb.createQuery(Client.class);
        Root<Client> c = cq.from(Client.class);

        List<Predicate> criteres = new ArrayList<>();
        if (nom != null && !nom.isBlank()) {
            criteres.add(cb.like(cb.lower(c.get("nom")), "%" + nom.toLowerCase() + "%"));
        }
        if (ville != null) {
            criteres.add(cb.equal(c.get("adresse").get("ville"), ville));
        }
        if (statut != null) {
            criteres.add(cb.equal(c.get("statut"), statut));
        }
        cq.where(criteres.toArray(Predicate[]::new)).orderBy(cb.asc(c.get("nom")));
        return em.createQuery(cq).getResultList();
    }

    // UPDATE en masse : contourne le contexte de persistance -> clear()
    public int augmenterPrixProduitsEnStock(BigDecimal facteur) {
        int nb = em.createQuery("update Produit p set p.prix = p.prix * :facteur where p.stock > 0")
                .setParameter("facteur", facteur)
                .executeUpdate();
        em.clear();
        return nb;
    }

    // SQL natif quand JPQL ne suffit pas
    @SuppressWarnings("unchecked")
    public List<Produit> produitsEnRupture(int seuil) {
        return em.createNativeQuery("select * from produit where stock < ?1", Produit.class)
                .setParameter(1, seuil)
                .getResultList();
    }
}

package com.formation.gestion.service;

import com.formation.gestion.entity.Adresse;
import com.formation.gestion.entity.Client;
import com.formation.gestion.entity.Commande;
import com.formation.gestion.entity.Produit;
import com.formation.gestion.entity.StatutClient;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/** Jeux de donnees partages par les TP 4 a 9 (la base est recreee a chaque lancement). */
@Service
public class JeuDeDonneesService {

    public record Catalogue(Long clientId, Long rizId, Long huileId, Long sucreId) {
    }

    @PersistenceContext
    private EntityManager em;

    /** 3 clients et 3 produits. Le client renvoye est Traore Moussa. */
    @Transactional
    public Catalogue creerClientsEtCatalogue() {
        Client traore = client("Traore Moussa", "m.traore@exemple.ci", "Abidjan", StatutClient.ACTIF);
        client("Kone Awa", "a.kone@exemple.ci", "Bouake", StatutClient.ACTIF);
        client("Diallo Fatou", "f.diallo@exemple.ci", "Abidjan", StatutClient.PROSPECT);

        Produit riz = new Produit("RIZ-25", "Riz 25 kg", new BigDecimal("18500"), 100);
        Produit huile = new Produit("HUI-5", "Huile 5 L", new BigDecimal("7500"), 50);
        Produit sucre = new Produit("SUC-50", "Sucre 50 kg", new BigDecimal("32000"), 40);
        em.persist(riz);
        em.persist(huile);
        em.persist(sucre);
        return new Catalogue(traore.getId(), riz.getId(), huile.getId(), sucre.getId());
    }

    /** Cree des commandes reparties sur les clients et dans le temps (une tous les 3 jours). */
    @Transactional
    public void creerCommandes(int nombre) {
        List<Client> clients = em.createQuery("select c from Client c order by c.id", Client.class).getResultList();
        List<Produit> produits = em.createQuery("select p from Produit p order by p.id", Produit.class).getResultList();
        for (int i = 0; i < nombre; i++) {
            Commande cmd = new Commande(String.format("CMD-%04d", i + 1), clients.get(i % clients.size()));
            cmd.setDateCommande(LocalDateTime.now().minusDays(3L * i));
            cmd.ajouterLigne(produits.get(i % produits.size()), (i % 5) + 1);
            cmd.ajouterLigne(produits.get((i + 1) % produits.size()), 1);
            em.persist(cmd);
        }
    }

    /**
     * Module 6 : beaucoup de clients avec plusieurs commandes chacun.
     * flush() + clear() reguliers pour ne pas saturer le contexte de persistance.
     */
    @Transactional
    public void genererClientsAvecCommandes(int nbClients, int commandesParClient) {
        String lot = UUID.randomUUID().toString().substring(0, 6);
        Produit produit = new Produit("GEN-" + lot, "Produit genere " + lot, new BigDecimal("1000"), 1_000_000);
        em.persist(produit);
        Long produitId = produit.getId();

        for (int i = 1; i <= nbClients; i++) {
            Client c = new Client("Client " + lot + "-" + i, "client-" + lot + "-" + i + "@exemple.ci",
                    new Adresse("Rue " + i, i % 2 == 0 ? "Abidjan" : "Yamoussoukro", "Cote d'Ivoire"));
            em.persist(c);
            for (int j = 1; j <= commandesParClient; j++) {
                Commande cmd = new Commande("G" + lot + "-" + i + "-" + j, c);
                cmd.ajouterLigne(produit, j);
                em.persist(cmd);
            }
            if (i % 50 == 0) {
                em.flush();
                em.clear();
                produit = em.find(Produit.class, produitId);   // le clear a detache le produit
            }
        }
    }

    private Client client(String nom, String email, String ville, StatutClient statut) {
        Client c = new Client(nom, email, new Adresse("Rue du Commerce", ville, "Cote d'Ivoire"));
        c.setStatut(statut);
        em.persist(c);
        return c;
    }
}

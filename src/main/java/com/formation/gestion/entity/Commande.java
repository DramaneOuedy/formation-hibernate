package com.formation.gestion.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** Module 3 : commande = parent d'une composition (cascade ALL + orphanRemoval sur les lignes). */
@Entity
public class Commande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String reference;

    private LocalDateTime dateCommande = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutCommande statut = StatutCommande.BROUILLON;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @OneToMany(mappedBy = "commande", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id")
    private List<LigneCommande> lignes = new ArrayList<>();

    protected Commande() {
    }

    public Commande(String reference, Client client) {
        this.reference = reference;
        this.client = client;
    }

    /** Garde les deux cotes de la relation synchronises. */
    public LigneCommande ajouterLigne(Produit produit, int quantite) {
        LigneCommande ligne = new LigneCommande(this, produit, quantite);
        lignes.add(ligne);
        return ligne;
    }

    public void retirerLigne(LigneCommande ligne) {
        lignes.remove(ligne);                // orphanRemoval -> DELETE au commit
    }

    public BigDecimal getMontantTotal() {
        return lignes.stream()
                .map(LigneCommande::getSousTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Long getId() { return id; }
    public String getReference() { return reference; }
    public LocalDateTime getDateCommande() { return dateCommande; }
    public void setDateCommande(LocalDateTime dateCommande) { this.dateCommande = dateCommande; }
    public StatutCommande getStatut() { return statut; }
    public void setStatut(StatutCommande statut) { this.statut = statut; }
    public Client getClient() { return client; }
    public List<LigneCommande> getLignes() { return lignes; }
}

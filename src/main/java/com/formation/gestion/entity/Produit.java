package com.formation.gestion.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/** Module 3 : produit du catalogue (aucune cascade vers lui : il vit sa propre vie). */
@Entity
public class Produit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String code;

    @Column(nullable = false, length = 120)
    private String nom;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal prix;

    private int stock;

    @ManyToMany
    @JoinTable(name = "produit_categorie",
               joinColumns = @JoinColumn(name = "produit_id"),
               inverseJoinColumns = @JoinColumn(name = "categorie_id"))
    private Set<Categorie> categories = new HashSet<>();

    protected Produit() {
    }

    public Produit(String code, String nom, BigDecimal prix, int stock) {
        this.code = code;
        this.nom = nom;
        this.prix = prix;
        this.stock = stock;
    }

    public void retirerStock(int quantite) {
        if (quantite > stock) {
            throw new IllegalStateException("Stock insuffisant pour " + code
                    + " (demande " + quantite + ", disponible " + stock + ")");
        }
        stock -= quantite;
    }

    public void ajouterCategorie(Categorie categorie) {
        categories.add(categorie);
        categorie.getProduits().add(this);
    }

    public Long getId() { return id; }
    public String getCode() { return code; }
    public String getNom() { return nom; }
    public BigDecimal getPrix() { return prix; }
    public void setPrix(BigDecimal prix) { this.prix = prix; }
    public int getStock() { return stock; }
    public Set<Categorie> getCategories() { return categories; }
}

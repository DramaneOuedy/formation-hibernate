package com.formation.gestion.entity;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Module 3 (pour aller plus loin) : relation ManyToMany avec Produit.
 * Module 7 : referentiel stable, lu souvent -> cache de 2e niveau.
 */
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Categorie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 60)
    private String nom;

    @ManyToMany(mappedBy = "categories")
    private Set<Produit> produits = new HashSet<>();

    protected Categorie() {
    }

    public Categorie(String nom) {
        this.nom = nom;
    }

    public Long getId() { return id; }
    public String getNom() { return nom; }
    public Set<Produit> getProduits() { return produits; }

    // equals/hashCode sur la cle metier (nom), stable avant et apres le persist
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Categorie other)) return false;
        return nom != null && nom.equals(other.getNom());   // getter : fonctionne aussi sur un proxy
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(nom);
    }
}

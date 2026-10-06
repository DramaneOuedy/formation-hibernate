package com.formation.gestion.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

/** Module 1 : premiere entite. */
@Entity
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;

    private String email;

    private LocalDateTime dateCreation;

    protected Utilisateur() {
        // constructeur sans argument obligatoire pour JPA
    }

    public Utilisateur(String nom, String email) {
        this.nom = nom;
        this.email = email;
        this.dateCreation = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public LocalDateTime getDateCreation() { return dateCreation; }

    @Override
    public String toString() {
        return "Utilisateur[id=" + id + ", nom=" + nom + ", email=" + email + "]";
    }
}

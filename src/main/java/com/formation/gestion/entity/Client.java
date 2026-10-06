package com.formation.gestion.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Module 2 : mapping avance (contraintes, index, enum, objet embarque). */
@Entity
@Table(name = "client",
       indexes = @Index(name = "idx_client_nom", columnList = "nom"),
       uniqueConstraints = @UniqueConstraint(name = "uk_client_email", columnNames = "email"))
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nom;

    @Column(nullable = false, length = 150)
    private String email;

    @Column(length = 20)
    private String telephone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutClient statut = StatutClient.PROSPECT;

    private LocalDate dateInscription = LocalDate.now();

    @Embedded
    private Adresse adresse;

    @Transient
    private boolean selectionne;              // etat d'ecran, non stocke

    // Module 3 : cote inverse, SANS cascade (on ne supprime pas l'historique avec le client)
    @OneToMany(mappedBy = "client")
    private List<Commande> commandes = new ArrayList<>();

    protected Client() {
    }

    public Client(String nom, String email, Adresse adresse) {
        this.nom = nom;
        this.email = email;
        this.adresse = adresse;
    }

    public Long getId() { return id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    public StatutClient getStatut() { return statut; }
    public void setStatut(StatutClient statut) { this.statut = statut; }
    public LocalDate getDateInscription() { return dateInscription; }
    public void setDateInscription(LocalDate dateInscription) { this.dateInscription = dateInscription; }
    public Adresse getAdresse() { return adresse; }
    public void setAdresse(Adresse adresse) { this.adresse = adresse; }
    public boolean isSelectionne() { return selectionne; }
    public void setSelectionne(boolean selectionne) { this.selectionne = selectionne; }
    public List<Commande> getCommandes() { return commandes; }
}

package com.formation.gestion.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/** Module 2 : objet valeur stocke dans la table du parent (pas d'identifiant propre). */
@Embeddable
public class Adresse {

    @Column(length = 150)
    private String rue;

    @Column(length = 80)
    private String ville;

    @Column(length = 80)
    private String pays;

    protected Adresse() {
    }

    public Adresse(String rue, String ville, String pays) {
        this.rue = rue;
        this.ville = ville;
        this.pays = pays;
    }

    public String getRue() { return rue; }
    public String getVille() { return ville; }
    public String getPays() { return pays; }
}

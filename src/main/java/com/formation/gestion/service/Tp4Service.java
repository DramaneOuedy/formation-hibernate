package com.formation.gestion.service;

import com.formation.gestion.dto.CommandeResumeDto;
import com.formation.gestion.entity.Client;
import com.formation.gestion.entity.Commande;
import com.formation.gestion.entity.Produit;
import com.formation.gestion.entity.StatutClient;
import com.formation.gestion.repository.CommandeQueries;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/** Module 4 : le service ouvre les transactions (lecture seule), le repository execute les requetes. */
@Service
@Transactional(readOnly = true)
public class Tp4Service {

    private final CommandeQueries queries;

    public Tp4Service(CommandeQueries queries) {
        this.queries = queries;
    }

    public Optional<Client> clientParEmail(String email) { return queries.clientParEmail(email); }

    public List<Commande> commandesDuClient(Long clientId) { return queries.commandesDuClient(clientId); }

    public List<Commande> commandesEntre(LocalDateTime debut, LocalDateTime fin) {
        return queries.commandesEntre(debut, fin);
    }

    public List<CommandeResumeDto> commandesAuDessusDe(BigDecimal min) { return queries.commandesAuDessusDe(min); }

    public List<Commande> page(int page, int taille) { return queries.page(page, taille); }

    public long compterCommandes() { return queries.compterCommandes(); }

    public Optional<Commande> detailComplet(Long id) { return queries.detailComplet(id); }

    public List<CommandeResumeDto> resumesEntre(LocalDateTime debut, LocalDateTime fin) {
        return queries.resumesEntre(debut, fin);
    }

    public List<Client> rechercherClients(String nom, String ville, StatutClient statut) {
        return queries.rechercherClients(nom, ville, statut);
    }

    public List<Produit> produitsEnRupture(int seuil) { return queries.produitsEnRupture(seuil); }

    @Transactional          // ecriture : surcharge le readOnly de la classe
    public int augmenterPrixProduitsEnStock(BigDecimal facteur) {
        return queries.augmenterPrixProduitsEnStock(facteur);
    }
}

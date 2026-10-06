package com.formation.gestion.service;

import com.formation.gestion.dto.ClientDto;
import com.formation.gestion.dto.ClientNbCommandes;
import com.formation.gestion.entity.Adresse;
import com.formation.gestion.entity.Client;
import com.formation.gestion.entity.StatutClient;
import com.formation.gestion.repository.ClientRepository;
import com.formation.gestion.repository.CommandeQueries;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/** Module 8 : couche Service = regles metier + transactions + conversion entite -> DTO. */
@Service
public class ClientService {

    private final ClientRepository repo;
    private final CommandeQueries queries;

    public ClientService(ClientRepository repo, CommandeQueries queries) {
        this.repo = repo;
        this.queries = queries;
    }

    @Transactional
    public ClientDto creer(String nom, String email, Adresse adresse) {
        if (repo.findByEmail(email).isPresent()) {
            throw new MetierException("Email deja utilise : " + email);
        }
        return ClientDto.from(repo.save(new Client(nom, email, adresse)));
    }

    /** Pas de save() : l'entite est geree, le dirty checking fait l'UPDATE. */
    @Transactional
    public ClientDto modifier(Long id, String nom, String telephone) {
        Client client = repo.findById(id)
                .orElseThrow(() -> new MetierException("Client introuvable : " + id));
        client.setNom(nom);
        client.setTelephone(telephone);
        return ClientDto.from(client);
    }

    @Transactional(readOnly = true)
    public List<ClientDto> rechercher(String nom, String ville, StatutClient statut) {
        return queries.rechercherClients(nom, ville, statut).stream().map(ClientDto::from).toList();
    }

    @Transactional(readOnly = true)
    public Page<ClientDto> parStatut(StatutClient statut, int page, int taille) {
        return repo.findByStatut(statut, PageRequest.of(page, taille, Sort.by("nom")))
                .map(ClientDto::from);
    }

    @Transactional(readOnly = true)
    public ClientNbCommandes detailAvecCommandes(Long id) {
        Client c = repo.findWithCommandesById(id)
                .orElseThrow(() -> new MetierException("Client introuvable : " + id));
        return new ClientNbCommandes(c.getNom(), (long) c.getCommandes().size());
    }

    @Transactional(readOnly = true)
    public List<ClientNbCommandes> nombreDeCommandesParClient() {
        return repo.compterCommandesParClient();
    }

    @Transactional
    public int desactiverInscritsAvant(LocalDate date) {
        return repo.changerStatutAvant(StatutClient.INACTIF, date);
    }
}

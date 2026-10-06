package com.formation.gestion.repository;

import com.formation.gestion.dto.ClientNbCommandes;
import com.formation.gestion.entity.Client;
import com.formation.gestion.entity.StatutClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/** Module 8 : Spring Data JPA genere l'implementation a partir de l'interface. */
public interface ClientRepository extends JpaRepository<Client, Long> {

    Optional<Client> findByEmail(String email);                       // requete deduite du nom

    Page<Client> findByStatut(StatutClient statut, Pageable pageable);

    @EntityGraph(attributePaths = "commandes")
    Optional<Client> findWithCommandesById(Long id);                  // evite le N+1

    @Query("""
            select new com.formation.gestion.dto.ClientNbCommandes(c.nom, count(o))
            from Client c left join c.commandes o
            group by c.id, c.nom
            order by c.id""")
    List<ClientNbCommandes> compterCommandesParClient();               // projection DTO

    @Modifying(clearAutomatically = true)
    @Query("update Client c set c.statut = :statut where c.dateInscription < :date")
    int changerStatutAvant(@Param("statut") StatutClient statut, @Param("date") LocalDate date);
}

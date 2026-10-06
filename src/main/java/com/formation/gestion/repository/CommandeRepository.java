package com.formation.gestion.repository;

import com.formation.gestion.dto.CommandeResumeDto;
import com.formation.gestion.entity.Commande;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/** Module 9 : listes en projection DTO, paginees. */
public interface CommandeRepository extends JpaRepository<Commande, Long> {

    @Query("""
            select new com.formation.gestion.dto.CommandeResumeDto(
                c.id, c.reference, c.dateCommande, cl.nom, sum(l.prixUnitaire * l.quantite))
            from Commande c join c.client cl join c.lignes l
            where cl.id = :clientId
            group by c.id, c.reference, c.dateCommande, cl.nom
            order by c.dateCommande desc""")
    List<CommandeResumeDto> resumesDuClient(@Param("clientId") Long clientId);

    @Query(value = """
            select new com.formation.gestion.dto.CommandeResumeDto(
                c.id, c.reference, c.dateCommande, cl.nom, sum(l.prixUnitaire * l.quantite))
            from Commande c join c.client cl join c.lignes l
            group by c.id, c.reference, c.dateCommande, cl.nom
            order by c.dateCommande desc, c.id""",
           countQuery = "select count(distinct c) from Commande c join c.lignes l")
    Page<CommandeResumeDto> resumes(Pageable pageable);
}

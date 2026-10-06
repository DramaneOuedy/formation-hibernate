package com.formation.gestion.repository;

import com.formation.gestion.entity.Client;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Module 8 : un repository ecrit a la main, pour comparer avec ClientRepository (Spring Data).
 * @Repository active la traduction des exceptions JPA en DataAccessException Spring.
 */
@Repository
public class JpaClientRepository {

    @PersistenceContext
    private EntityManager em;

    public Optional<Client> findByEmail(String email) {
        return em.createQuery("select c from Client c where c.email = :email", Client.class)
                .setParameter("email", email)
                .getResultStream()
                .findFirst();
    }

    public Client save(Client client) {
        if (client.getId() == null) {
            em.persist(client);
            return client;
        }
        return em.merge(client);
    }
}

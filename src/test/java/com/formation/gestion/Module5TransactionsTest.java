package com.formation.gestion;

import com.formation.gestion.service.CommandeService;
import com.formation.gestion.service.JeuDeDonneesService;
import com.formation.gestion.service.StockInsuffisantException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class Module5TransactionsTest extends BaseTest {

    @Autowired
    JeuDeDonneesService jeu;

    @Autowired
    CommandeService service;

    @Test
    void rollbackSurRuntimeExceptionEtPiegeDesExceptionsControlees() {
        JeuDeDonneesService.Catalogue cat = jeu.creerClientsEtCatalogue();
        Map<Long, Integer> ok = new LinkedHashMap<>();
        ok.put(cat.rizId(), 2);
        ok.put(cat.huileId(), 1);
        service.passerCommande(cat.clientId(), ok);
        assertEquals(98, service.stock(cat.rizId()));
        assertEquals(1, service.nombreDeCommandes());

        Map<Long, Integer> ko = new LinkedHashMap<>();
        ko.put(cat.rizId(), 1);
        ko.put(cat.huileId(), 9_999);
        assertThrows(IllegalStateException.class, () -> service.passerCommande(cat.clientId(), ko));
        assertEquals(98, service.stock(cat.rizId()), "rollback : stock inchange");
        assertEquals(1, service.nombreDeCommandes());

        assertThrows(StockInsuffisantException.class, () -> service.passerCommandeSansRollback(cat.clientId(), ko));
        assertEquals(97, service.stock(cat.rizId()), "exception controlee : la transaction a ete VALIDEE");

        assertThrows(StockInsuffisantException.class, () -> service.passerCommandeAvecRollback(cat.clientId(), ko));
        assertEquals(97, service.stock(cat.rizId()), "rollbackFor : stock inchange");
        assertEquals(1, service.nombreDeCommandes());
    }
}

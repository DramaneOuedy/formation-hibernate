package com.formation.gestion.service;

/** Module 5 (variante) : exception CONTROLEE -> par defaut, Spring VALIDE la transaction. */
public class StockInsuffisantException extends Exception {

    public StockInsuffisantException(String message) {
        super(message);
    }
}

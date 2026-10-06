package com.formation.gestion.service;

/** Module 8 : exception metier explicite (RuntimeException -> rollback automatique). */
public class MetierException extends RuntimeException {

    public MetierException(String message) {
        super(message);
    }
}

package com.ciblorgasport.participantsservice.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Gestion d'erreurs minimaliste pour renvoyer des réponses propres au front.
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<?> handleIllegalState(IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                "error", ex.getMessage() == null ? "Conflit d'état" : ex.getMessage()
        ));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgument(IllegalArgumentException ex) {
        // Par convention dans ce microservice :
        // - "introuvable" => 404
        // - autres => 400
        HttpStatus status = ex.getMessage() != null && ex.getMessage().toLowerCase().contains("introuvable")
                ? HttpStatus.NOT_FOUND
                : HttpStatus.BAD_REQUEST;

        return ResponseEntity.status(status).body(Map.of(
                "error", ex.getMessage() == null ? "Erreur" : ex.getMessage()
        ));
    }
}

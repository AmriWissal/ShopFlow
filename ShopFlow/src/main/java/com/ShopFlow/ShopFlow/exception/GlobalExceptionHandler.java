package com.ShopFlow.ShopFlow.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Gestionnaire global des exceptions pour toute l'application.
 * Intercepte les exceptions levées par les contrôleurs et retourne des réponses HTTP structurées.
 * Utilise @RestControllerAdvice pour s'appliquer à tous les @RestController.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Gère les exceptions de type ResourceNotFoundException.
     * Retourne un code HTTP 404 (Not Found) avec un message d'erreur.
     * 
     * @param ex L'exception levée
     * @return ResponseEntity avec le message d'erreur et le statut 404
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", HttpStatus.NOT_FOUND.value());
        errorResponse.put("error", "Ressource non trouvée");
        errorResponse.put("message", ex.getMessage()); // Message exact de l'exception
        
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * Gère les exceptions de type AccessDeniedException (accès refusé).
     * Retourne un code HTTP 403 (Forbidden) avec un message d'erreur.
     * 
     * @param ex L'exception levée
     * @return ResponseEntity avec le message d'erreur et le statut 403
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDeniedException(AccessDeniedException ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", HttpStatus.FORBIDDEN.value());
        errorResponse.put("error", "Accès refusé");
        errorResponse.put("message", ex.getMessage()); // Message exact de l'exception
        
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    /**
     * Gère toutes les exceptions de type RuntimeException (erreurs métier).
     * Retourne un code HTTP 400 (Bad Request) avec le message exact de l'exception.
     * 
     * Exemples d'utilisation :
     * - Coupon déjà utilisé dans des commandes
     * - Stock insuffisant pour confirmer une commande
     * - Panier vide lors de la création d'une commande
     * - Coupon expiré ou inactif
     * 
     * @param ex L'exception levée
     * @return ResponseEntity avec le message d'erreur exact et le statut 400
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
        errorResponse.put("error", "Erreur de validation");
        errorResponse.put("message", ex.getMessage()); // ✅ Message exact de l'exception pour affichage frontend
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Gère toutes les autres exceptions non prévues (fallback).
     * Retourne un code HTTP 500 (Internal Server Error).
     * 
     * @param ex L'exception levée
     * @return ResponseEntity avec un message d'erreur générique et le statut 500
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGlobalException(Exception ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorResponse.put("error", "Erreur interne du serveur");
        errorResponse.put("message", "Une erreur inattendue s'est produite : " + ex.getMessage());
        
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

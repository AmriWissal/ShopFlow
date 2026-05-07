package com.ShopFlow.ShopFlow.controller;

import com.ShopFlow.ShopFlow.dto.request.LoginRequest;
import com.ShopFlow.ShopFlow.dto.request.RegisterRequest;
import com.ShopFlow.ShopFlow.dto.TokenRefreshRequest;
import com.ShopFlow.ShopFlow.dto.response.AuthResponse;
import com.ShopFlow.ShopFlow.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ShopFlow.ShopFlow.dto.request.UserUpdateRequest;
import com.ShopFlow.ShopFlow.dto.response.UserResponse;

@RestController // Déclare cette classe comme un contrôleur REST
@RequestMapping("/api/auth") // Définit la route de base pour tous les endpoints d'authentification
@RequiredArgsConstructor // Génère un constructeur avec les arguments obligatoires (final)
@CrossOrigin(origins = "*", allowedHeaders = "*") // Autorise les requêtes cross-origin pour le frontend
@Tag(name = "Authentication", description = "Endpoints pour l'inscription, la connexion et la gestion des tokens") // Documentation Swagger
public class AuthController {

    private final AuthService authService; // Injection du service d'authentification

    @PutMapping("/profile")
    @Operation(summary = "Mise à jour du profil utilisateur")
    public UserResponse updateProfile(@Valid @RequestBody UserUpdateRequest request) {
        return authService.updateProfile(request);
    }

    /**
     * Endpoint pour l'inscription d'un client ou d'un vendeur.
     */
    @PostMapping("/register") // Définit une requête POST sur /api/auth/register
    @Operation(summary = "Inscription client ou vendeur") // Description Swagger
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) { // Valide le corps de la requête
        return authService.register(request); // Appelle la méthode d'inscription du service
    }

    /**
     * Endpoint pour la connexion retournant l'access_token et le refresh_token.
     */
    @PostMapping("/login") // Définit une requête POST sur /api/auth/login
    @Operation(summary = "Connexion retournant access_token + refresh_token") // Description Swagger
    public AuthResponse login(@Valid @RequestBody LoginRequest request) { // Valide le corps de la requête
        return authService.login(request); // Appelle la méthode de connexion du service
    }

    /**
     * Endpoint pour le renouvellement du token via le refresh token.
     */
    @PostMapping("/refresh") // Définit une requête POST sur /api/auth/refresh
    @Operation(summary = "Renouvellement du token") // Description Swagger
    public AuthResponse refreshToken(@RequestBody TokenRefreshRequest request) { // Reçoit la requête de rafraîchissement
        return authService.refreshToken(request); // Appelle le service pour générer un nouveau token
    }

    /**
     * Endpoint pour l'invalidation du refresh token (déconnexion).
     */
    @PostMapping("/logout") // Définit une requête POST sur /api/auth/logout
    @Operation(summary = "Invalidation du refresh token") // Description Swagger
    public ResponseEntity<Void> logout(@RequestBody TokenRefreshRequest request) { // Reçoit le token à invalider
        authService.logout(request.getRefreshToken()); // Appelle le service pour la déconnexion
        return ResponseEntity.noContent().build(); // Retourne un statut 204 No Content
    }
}
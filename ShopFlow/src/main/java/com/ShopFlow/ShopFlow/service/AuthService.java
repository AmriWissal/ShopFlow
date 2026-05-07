package com.ShopFlow.ShopFlow.service;

import com.ShopFlow.ShopFlow.dto.TokenRefreshRequest;
import com.ShopFlow.ShopFlow.dto.request.LoginRequest;
import com.ShopFlow.ShopFlow.dto.request.RegisterRequest;
import com.ShopFlow.ShopFlow.dto.response.AuthResponse;
import com.ShopFlow.ShopFlow.dto.response.UserResponse;
import com.ShopFlow.ShopFlow.entity.User;
import com.ShopFlow.ShopFlow.mapper.UserMapper;
import com.ShopFlow.ShopFlow.repository.UserRepository;
import com.ShopFlow.ShopFlow.security.CustomUserDetails;
import com.ShopFlow.ShopFlow.security.JwtUtils;
import com.ShopFlow.ShopFlow.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import com.ShopFlow.ShopFlow.dto.request.UserUpdateRequest;

@Service // Indique que cette classe est un service Spring
@RequiredArgsConstructor // Génère un constructeur pour les injections automatiques des champs final
@Transactional // Rend toutes les méthodes de la classe transactionnelles par défaut
public class AuthService {

    private final UserRepository userRepository; // Accès aux données utilisateurs
    private final PasswordEncoder passwordEncoder; // Utilitaire de hachage de mot de passe
    private final JwtUtils jwtUtils; // Utilitaire pour la gestion des tokens JWT
    private final AuthenticationManager authenticationManager; // Gestionnaire d'authentification Spring Security
    private final UserMapper userMapper; // Convertisseur Entité <-> DTO
    private final SecurityUtils securityUtils;

    /**
     * Met à jour les informations de l'utilisateur connecté.
     */
    public UserResponse updateProfile(UserUpdateRequest request) {
        User user = securityUtils.getCurrentUser();
        if (user == null) {
            throw new RuntimeException("Utilisateur non authentifié");
        }

        // Mise à jour des champs
        if (request.getFirstName() != null && !request.getFirstName().isBlank()) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null && !request.getLastName().isBlank()) {
            user.setLastName(request.getLastName());
        }
        // Note: fullName est calculé automatiquement par getFullName()

        User updatedUser = userRepository.save(user);
        return userMapper.toResponse(updatedUser);
    }

    /**
     * Enregistre un nouvel utilisateur (Client ou Vendeur).
     */
    public AuthResponse register(RegisterRequest request) {
        // Vérification de l'unicité de l'email dans la base de données
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email déjà utilisé"); // Erreur si l'email existe déjà
        }

        // Création de l'entité utilisateur avec les données de la requête
        User user = User.builder()
                .firstName(request.getFirstName()) // Nom
                .lastName(request.getLastName()) // Prénom
                .email(request.getEmail()) // Email
                .password(passwordEncoder.encode(request.getPassword())) // Mot de passe haché
                .role(request.getRole()) // Rôle (CLIENT ou SELLER)
                .active(true) // L'utilisateur est actif par défaut
                .build();

        userRepository.save(user); // Sauvegarde l'utilisateur en base de données
        CustomUserDetails userDetails = new CustomUserDetails(user); // Crée l'objet de sécurité utilisateur

        // Génération des tokens JWT d'accès et de rafraîchissement
        return AuthResponse.builder()
                .accessToken(jwtUtils.generateToken(userDetails)) // Génère l'access token
                .refreshToken(jwtUtils.generateRefreshToken(userDetails)) // Génère le refresh token
                .user(userMapper.toResponse(user)) // Convertit l'entité en DTO de réponse
                .build();
    }

    /**
     * Authentifie un utilisateur et retourne ses tokens.
     */
    public AuthResponse login(LoginRequest request) {
        // Authentification via Spring Security (vérifie email et mot de passe)
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        // Récupère l'utilisateur depuis la base de données après authentification réussie
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        CustomUserDetails userDetails = new CustomUserDetails(user); // Prépare les détails de sécurité

        // Retourne la réponse contenant les nouveaux tokens
        return AuthResponse.builder()
                .accessToken(jwtUtils.generateToken(userDetails)) // Access token
                .refreshToken(jwtUtils.generateRefreshToken(userDetails)) // Refresh token
                .user(userMapper.toResponse(user)) // Informations utilisateur
                .build();
    }

    /**
     * Renouvelle l'access token à partir d'un refresh token valide.
     */
    public AuthResponse refreshToken(TokenRefreshRequest request) {
        String refreshToken = request.getRefreshToken(); // Récupère le refresh token de la requête
        String userEmail = jwtUtils.extractUsername(refreshToken); // Extrait l'email du token

        // Recherche l'utilisateur associé à cet email
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        CustomUserDetails userDetails = new CustomUserDetails(user); // Prépare les détails de sécurité

        // Valide le token de rafraîchissement
        if (jwtUtils.isTokenValid(refreshToken, userDetails)) {
            String newAccessToken = jwtUtils.generateToken(userDetails); // Génère un nouvel access token
            return AuthResponse.builder()
                    .accessToken(newAccessToken) // Nouveau token d'accès
                    .refreshToken(refreshToken) // Garde le même refresh token
                    .user(userMapper.toResponse(user)) // Infos utilisateur
                    .build();
        }
        throw new RuntimeException("Refresh token invalide"); // Erreur si le token n'est plus valide
    }

    /**
     * Gère la déconnexion en invalidant le token (logique simplifiée).
     */
    public void logout(String refreshToken) {
        // Dans une implémentation réelle, on pourrait blacklister le token ici
        // Pour ce projet, on peut simplement s'assurer que le client supprime le token
    }
}

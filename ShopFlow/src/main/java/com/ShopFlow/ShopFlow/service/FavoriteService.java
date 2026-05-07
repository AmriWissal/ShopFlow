package com.ShopFlow.ShopFlow.service;

import com.ShopFlow.ShopFlow.dto.response.FavoriteResponse;
import com.ShopFlow.ShopFlow.entity.Favorite;
import com.ShopFlow.ShopFlow.entity.Product;
import com.ShopFlow.ShopFlow.entity.User;
import com.ShopFlow.ShopFlow.exception.ResourceNotFoundException;
import com.ShopFlow.ShopFlow.mapper.FavoriteMapper;
import com.ShopFlow.ShopFlow.repository.FavoriteRepository;
import com.ShopFlow.ShopFlow.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final ProductRepository productRepository;
    private final FavoriteMapper favoriteMapper;

    /**
     * Ajoute un produit aux favoris
     */
    @Transactional
    public FavoriteResponse addToFavorites(User user, Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Produit non trouvé"));

        // Vérifier si déjà en favoris
        if (favoriteRepository.existsByUserAndProduct(user, product)) {
            throw new RuntimeException("Ce produit est déjà dans vos favoris");
        }

        Favorite favorite = Favorite.builder()
                .user(user)
                .product(product)
                .build();

        return favoriteMapper.toResponse(favoriteRepository.save(favorite));
    }

    /**
     * Retire un produit des favoris
     */
    @Transactional
    public void removeFromFavorites(User user, Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Produit non trouvé"));

        favoriteRepository.deleteByUserAndProduct(user, product);
    }

    /**
     * Récupère tous les favoris d'un utilisateur
     */
    @Transactional(readOnly = true)
    public List<FavoriteResponse> getUserFavorites(User user) {
        return favoriteRepository.findByUserOrderByCreatedAtDesc(user).stream()
                .map(favoriteMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Vérifie si un produit est dans les favoris
     */
    @Transactional(readOnly = true)
    public boolean isFavorite(User user, Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Produit non trouvé"));
        
        return favoriteRepository.existsByUserAndProduct(user, product);
    }

    /**
     * Toggle favori (ajoute si absent, retire si présent)
     */
    @Transactional
    public boolean toggleFavorite(User user, Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Produit non trouvé"));

        if (favoriteRepository.existsByUserAndProduct(user, product)) {
            favoriteRepository.deleteByUserAndProduct(user, product);
            return false; // Retiré des favoris
        } else {
            Favorite favorite = Favorite.builder()
                    .user(user)
                    .product(product)
                    .build();
            favoriteRepository.save(favorite);
            return true; // Ajouté aux favoris
        }
    }
}

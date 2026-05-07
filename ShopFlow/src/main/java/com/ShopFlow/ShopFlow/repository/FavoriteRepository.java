package com.ShopFlow.ShopFlow.repository;

import com.ShopFlow.ShopFlow.entity.Favorite;
import com.ShopFlow.ShopFlow.entity.Product;
import com.ShopFlow.ShopFlow.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    
    /**
     * Trouve tous les favoris d'un utilisateur
     */
    List<Favorite> findByUserOrderByCreatedAtDesc(User user);
    
    /**
     * Vérifie si un produit est dans les favoris d'un utilisateur
     */
    boolean existsByUserAndProduct(User user, Product product);
    
    /**
     * Trouve un favori spécifique
     */
    Optional<Favorite> findByUserAndProduct(User user, Product product);
    
    /**
     * Supprime un favori spécifique
     */
    void deleteByUserAndProduct(User user, Product product);
}

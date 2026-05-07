package com.ShopFlow.ShopFlow.repository;

import com.ShopFlow.ShopFlow.entity.Product;
import com.ShopFlow.ShopFlow.entity.User;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    /**
     * Récupère un produit par ID avec ses catégories et reviews.
     */
    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.categories WHERE p.id = :id")
    Optional<Product> findByIdWithDetails(@Param("id") Long id);

    /**
     * Récupère tous les produits avec leurs catégories.
     */
    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.categories")
    List<Product> findAllWithCategories();

    /**
     * Récupère tous les produits actifs avec leurs catégories.
     */
    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.categories WHERE p.active = true")
    List<Product> findByActiveTrueWithCategories();

    /**
     * Récupère les produits actifs d'un vendeur avec catégories.
     */
    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.categories WHERE p.seller = :seller AND p.active = true")
    List<Product> findBySellerAndActiveTrueWithCategories(@Param("seller") User seller);

    /**
     * Recherche les produits actifs par nom ou description.
     */
    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.categories WHERE (LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND p.active = true")
    List<Product> findByNameOrDescriptionContainingIgnoreCaseAndActiveTrue(@Param("keyword") String keyword);

    /**
     * Recherche avec catégories (alias pour compatibilité).
     */
    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.categories WHERE (LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND p.active = true")
    List<Product> searchWithCategories(@Param("keyword") String keyword);

    /**
     * Top selling products.
     */
    @Query("SELECT p FROM Product p JOIN OrderItem oi ON oi.product = p GROUP BY p ORDER BY SUM(oi.quantity) DESC")
    List<Product> findTopSellingProducts(Pageable pageable);

    // Méthodes de statistiques
    @Query("SELECT c.name, COUNT(p) FROM Product p JOIN p.categories c GROUP BY c.name")
    List<Object[]> countProductsByCategory();

    long countBySellerAndActiveTrue(User seller);

    @Query("SELECT c.name, COUNT(p) FROM Product p JOIN p.categories c WHERE p.seller = :seller GROUP BY c.name")
    List<Object[]> countSellerProductsByCategory(@Param("seller") User seller);
}

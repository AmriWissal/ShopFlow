package com.ShopFlow.ShopFlow.repository;

import com.ShopFlow.ShopFlow.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByParentIsNull();

    /**
     * Récupère une catégorie avec ses produits et sous-catégories chargés.
     * Utilisé pour la suppression afin d'éviter les LazyInitializationException.
     */
    @Query("SELECT c FROM Category c " +
           "LEFT JOIN FETCH c.products " +
           "LEFT JOIN FETCH c.subCategories " +
           "WHERE c.id = :id")
    Optional<Category> findByIdWithProductsAndSubCategories(@Param("id") Long id);

    /**
     * Supprime toutes les associations entre une catégorie et ses produits.
     * Utilisé avant la suppression de la catégorie pour éviter les contraintes FK.
     */
    @Modifying
    @Query(value = "DELETE FROM product_categories WHERE category_id = :categoryId", nativeQuery = true)
    void deleteProductCategoryAssociations(@Param("categoryId") Long categoryId);

    /**
     * Met à jour le parent des sous-catégories avant suppression.
     */
    @Modifying
    @Query("UPDATE Category c SET c.parent = :newParent WHERE c.parent.id = :categoryId")
    void updateSubCategoriesParent(@Param("categoryId") Long categoryId, @Param("newParent") Category newParent);
}
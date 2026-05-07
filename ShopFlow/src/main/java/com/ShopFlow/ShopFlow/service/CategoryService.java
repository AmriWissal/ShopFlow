package com.ShopFlow.ShopFlow.service;

import com.ShopFlow.ShopFlow.dto.response.CategoryResponse;
import com.ShopFlow.ShopFlow.entity.Category;
import com.ShopFlow.ShopFlow.entity.User;
import com.ShopFlow.ShopFlow.entity.enums.Role;
import com.ShopFlow.ShopFlow.exception.ResourceNotFoundException;
import com.ShopFlow.ShopFlow.mapper.CategoryMapper;
import com.ShopFlow.ShopFlow.repository.CategoryRepository;
import com.ShopFlow.ShopFlow.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service // Déclare la classe comme service Spring
@RequiredArgsConstructor // Génère le constructeur pour l'injection des dépendances
@Transactional(readOnly = true) // Opérations en lecture seule par défaut pour optimiser
public class CategoryService {

    private final CategoryRepository categoryRepository; // Repository pour l'accès aux données
    private final SecurityUtils securityUtils; // Utilitaire pour l'utilisateur actuel
    private final CategoryMapper categoryMapper; // Convertisseur pour les DTOs

    /**
     * Récupère l'arbre des catégories (Racines et leurs enfants).
     */
    public List<CategoryResponse> getCategoryTree() {
        // Récupère toutes les catégories qui n'ont pas de parent (racines)
        List<Category> roots = categoryRepository.findByParentIsNull();

        if (roots == null) roots = List.of(); // Liste vide si aucune racine

        // Le chargement est LAZY, MapStruct s'occupera de naviguer dans l'arbre
        return categoryMapper.toResponseTree(roots); // Convertit en structure hiérarchique DTO
    }

    /**
     * Crée une nouvelle catégorie (ADMIN uniquement selon contrôleur).
     */
    @Transactional // Nécessite une transaction en écriture
    public CategoryResponse createCategory(Category category) {
        // Enregistre et retourne la nouvelle catégorie convertie en DTO
        return categoryMapper.toResponse(categoryRepository.save(category));
    }

    /**
     * Met à jour une catégorie existante.
     */
    @Transactional // Nécessite une transaction en écriture
    public CategoryResponse updateCategory(Long id, Category details) {
        // Recherche la catégorie ou lance une exception si absente
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Catégorie non trouvée"));

        // Mise à jour des informations de base
        category.setName(details.getName()); // Nouveau nom
        category.setDescription(details.getDescription()); // Nouvelle description
        category.setParent(details.getParent()); // Nouveau parent (si modifié)

        // Sauvegarde et retourne le résultat converti
        return categoryMapper.toResponse(categoryRepository.save(category));
    }

    /**
     * Supprime une catégorie de la base de données.
     * Dissocie automatiquement tous les produits associés avant la suppression.
     * Les sous-catégories sont remontées au parent de la catégorie supprimée.
     */
    @Transactional // Nécessite une transaction en écriture
    public void deleteCategory(Long id) {
        // Vérifier que la catégorie existe
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Catégorie non trouvée avec l'ID: " + id));

        // 1. Supprimer toutes les associations avec les produits (table de jointure product_categories)
        // Cela dissocie les produits de la catégorie sans les supprimer
        categoryRepository.deleteProductCategoryAssociations(id);

        // 2. Remonter les sous-catégories au parent (ou null si catégorie racine)
        // Les sous-catégories deviennent des catégories racines ou sont rattachées au grand-parent
        Category parent = category.getParent();
        categoryRepository.updateSubCategoriesParent(id, parent);

        // 3. Suppression physique de la catégorie
        categoryRepository.deleteById(id);
    }
}
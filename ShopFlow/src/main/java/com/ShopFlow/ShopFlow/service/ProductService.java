package com.ShopFlow.ShopFlow.service;

import com.ShopFlow.ShopFlow.dto.request.ProductRequest;
import com.ShopFlow.ShopFlow.dto.response.ProductResponse;
import com.ShopFlow.ShopFlow.entity.*;
import com.ShopFlow.ShopFlow.entity.enums.Role;
import com.ShopFlow.ShopFlow.exception.ResourceNotFoundException;
import com.ShopFlow.ShopFlow.mapper.ProductMapper;
import com.ShopFlow.ShopFlow.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final ProductMapper productMapper;

    public Page<ProductResponse> getProducts(
            String q,
            Long categoryId,
            Double minPrice,
            Double maxPrice,
            Long sellerId,
            boolean promoOnly,
            Pageable pageable) {

        Specification<Product> spec = Specification.where(null);

        // 🔍 Recherche texte
        if (q != null && !q.trim().isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.or(
                    cb.like(cb.lower(root.get("name")), "%" + q.toLowerCase() + "%"),
                    cb.like(cb.lower(root.get("description")), "%" + q.toLowerCase() + "%")
            ));
        }

        // 🗂 Filtre catégorie
        if (categoryId != null) {
            spec = spec.and((root, query, cb) -> {
                query.distinct(true); // 🔥 IMPORTANT pour éviter doublons
                return cb.equal(root.join("categories").get("id"), categoryId);
            });
        }

        // 💰 Prix min
        if (minPrice != null) {
            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("price"), minPrice)
            );
        }

        // 💰 Prix max
        if (maxPrice != null) {
            spec = spec.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("price"), maxPrice)
            );
        }

        // 👤 Seller
        if (sellerId != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("seller").get("id"), sellerId)
            );
        }

        // 🏷 Promo only
        if (promoOnly) {
            spec = spec.and((root, query, cb) ->
                    cb.isNotNull(root.get("promoPrice"))
            );
        }

        // ✅ Produits actifs seulement
        spec = spec.and((root, query, cb) ->
                cb.isTrue(root.get("active"))
        );

        // 🚀 Execution + mapping
        return productRepository.findAll(spec, pageable)
                .map(productMapper::toResponse);
    }

    /**
     * Récupère les produits d'un vendeur spécifique.
     */
    @Transactional(readOnly = true)
    public List<ProductResponse> getProductsBySeller(User seller) {
        if (seller == null) return new ArrayList<>();
        return productRepository.findBySellerAndActiveTrueWithCategories(seller)
                .stream()
                .map(productMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Récupère les produits d'un vendeur spécifique avec pagination.
     */
    @Transactional(readOnly = true)
    public Page<ProductResponse> getProductsBySellerPaginated(User seller, Pageable pageable) {
        if (seller == null) return Page.empty();
        
        Specification<Product> spec = Specification.where(null);
        
        // Filtre par vendeur
        spec = spec.and((root, query, cb) -> cb.equal(root.get("seller").get("id"), seller.getId()));
        
        // Produits actifs seulement
        spec = spec.and((root, query, cb) -> cb.isTrue(root.get("active")));
        
        return productRepository.findAll(spec, pageable)
                .map(productMapper::toResponse);
    }

    /**
     * Récupère un produit complet par son ID.
     */
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produit non trouvé"));

        if (!product.isActive()) {
            throw new ResourceNotFoundException("Produit inactif");
        }

        return productMapper.toResponse(product);
    }

    /**
     * Crée un nouveau produit.
     */
    @Transactional
    public ProductResponse createProduct(ProductRequest request, User currentUser) {
        if (currentUser == null) {
            throw new AccessDeniedException("Utilisateur non authentifié");
        }

        Product product = productMapper.toEntity(request);
        
        User seller;
        if (currentUser.getRole() == Role.ADMIN && request.getSellerId() != null) {
            seller = userRepository.findById(request.getSellerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Vendeur non trouvé"));
        } else {
            seller = userRepository.findById(currentUser.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
        }
        
        if (seller.getRole() != Role.SELLER && seller.getRole() != Role.ADMIN) {
            throw new IllegalArgumentException("L'utilisateur n'est pas un vendeur");
        }
        
        product.setSeller(seller);
        product.setActive(true);

        if (request.getCategoryIds() != null && !request.getCategoryIds().isEmpty()) {
            Set<Category> categories = new HashSet<>(categoryRepository.findAllById(request.getCategoryIds()));
            if (categories.isEmpty()) {
                throw new ResourceNotFoundException("Catégorie non trouvée");
            }
            product.setCategories(categories);
        } else {
            throw new IllegalArgumentException("Une catégorie est requise");
        }

        try {
            return productMapper.toResponse(productRepository.save(product));
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la sauvegarde : " + e.getMessage(), e);
        }
    }

    /**
     * Met à jour un produit.
     */
    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request, User user) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produit non trouvé"));

        if (!product.getSeller().getId().equals(user.getId()) && user.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("Accès refusé");
        }

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setPromoPrice(request.getPromoPrice());
        product.setStock(request.getStock());

        if (request.getCategoryIds() != null && !request.getCategoryIds().isEmpty()) {
            product.setCategories(new HashSet<>(categoryRepository.findAllById(request.getCategoryIds())));
        }

        return productMapper.toResponse(productRepository.save(product));
    }

    /**
     * Soft delete.
     */
    @Transactional
    public void deleteProduct(Long id, User user) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produit non trouvé"));

        if (!product.getSeller().getId().equals(user.getId()) && user.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("Accès refusé");
        }

        product.setActive(false);
        productRepository.save(product);
    }

    /**
     * Recherche les produits par mot-clé.
     */
    @Transactional(readOnly = true)
    public List<ProductResponse> searchProducts(String query) {
        return productRepository.searchWithCategories(query)
                .stream()
                .map(productMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Récupère les produits les plus vendus (top 10).
     */
    @Transactional(readOnly = true)
    public List<ProductResponse> getTopSellingProducts() {
        return productRepository.findTopSellingProducts(PageRequest.of(0, 10))
                .stream()
                .map(productMapper::toResponse)
                .collect(Collectors.toList());
    }
}
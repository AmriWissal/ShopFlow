package com.ShopFlow.ShopFlow.service;

import com.ShopFlow.ShopFlow.dto.request.ReviewRequest;
import com.ShopFlow.ShopFlow.dto.response.ReviewResponse;
import com.ShopFlow.ShopFlow.entity.Product;
import com.ShopFlow.ShopFlow.entity.Review;
import com.ShopFlow.ShopFlow.entity.User;
import com.ShopFlow.ShopFlow.entity.enums.OrderStatus;
import com.ShopFlow.ShopFlow.exception.ResourceNotFoundException;
import com.ShopFlow.ShopFlow.mapper.ReviewMapper;
import com.ShopFlow.ShopFlow.repository.OrderRepository;
import com.ShopFlow.ShopFlow.repository.ProductRepository;
import com.ShopFlow.ShopFlow.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service // Service pour la gestion des avis clients
@RequiredArgsConstructor // Injection automatique
public class ReviewService {

    private final ReviewRepository reviewRepository; // Accès aux avis
    private final ProductRepository productRepository; // Accès aux produits
    private final OrderRepository orderRepository; // Accès aux commandes pour vérification d'achat
    private final ReviewMapper reviewMapper; // Mapping DTO

    /**
     * Ajoute un avis si le client a déjà acheté le produit (Achat vérifié).
     */
    @Transactional // Transactionnel
    public ReviewResponse addReview(User customer, ReviewRequest request) {
        // Vérifie si le produit existe
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Produit non trouvé"));

        // Vérifie si le client a déjà laissé un avis pour ce produit
        boolean hasAlreadyReviewed = reviewRepository.findByProductAndApprovedTrue(product).stream()
                .anyMatch(review -> review.getCustomer().getId().equals(customer.getId()));

        if (hasAlreadyReviewed) {
            throw new RuntimeException("Vous avez déjà laissé un avis pour ce produit");
        }

        // Logique "Achat vérifié" : Le client doit avoir une commande livrée contenant ce produit
        boolean hasPurchased = orderRepository.findByCustomerOrderByOrderDateDesc(customer).stream()
                .filter(o -> o.getStatus() == OrderStatus.DELIVERED) // Commande livrée
                .flatMap(o -> o.getOrderItems().stream()) // Parcourt tous les articles de ces commandes
                .anyMatch(item -> item.getProduct().getId().equals(product.getId())); // Vérifie la présence du produit

        if (!hasPurchased) {
            throw new RuntimeException("Vous devez avoir acheté et reçu ce produit pour laisser un avis");
        }

        Review review = reviewMapper.toEntity(request); // DTO -> Entité
        review.setCustomer(customer); // Affecte le client
        review.setProduct(product); // Lie au produit
        review.setApproved(false); // Doit être approuvé par l'admin

        return reviewMapper.toResponse(reviewRepository.save(review)); // Sauvegarde et retourne DTO
    }

    /**
     * Liste les avis validés par l'admin pour un produit donné.
     */
    public List<ReviewResponse> getApprovedReviewsForProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Produit non trouvé"));
        // Filtre les avis approuvés uniquement
        return reviewRepository.findByProductAndApprovedTrue(product).stream()
                .map(reviewMapper::toResponse) // Conversion
                .collect(Collectors.toList());
    }

    /**
     * Liste tous les avis en attente de validation (pour l'admin).
     */
    @Transactional(readOnly = true)
    public List<ReviewResponse> getPendingReviews() {
        return reviewRepository.findAll().stream()
                .filter(review -> !review.isApproved()) // Filtre les avis non approuvés
                .map(review -> {
                    // Force le chargement des relations LAZY
                    if (review.getCustomer() != null) {
                        review.getCustomer().getFullName();
                    }
                    if (review.getProduct() != null) {
                        review.getProduct().getName();
                    }
                    return reviewMapper.toResponse(review);
                })
                .collect(Collectors.toList());
    }

    /**
     * Liste tous les avis (approuvés et en attente) pour l'admin.
     */
    @Transactional(readOnly = true)
    public List<ReviewResponse> getAllReviews() {
        return reviewRepository.findAll().stream()
                .map(review -> {
                    // Force le chargement des relations LAZY
                    if (review.getCustomer() != null) {
                        review.getCustomer().getFullName();
                    }
                    if (review.getProduct() != null) {
                        review.getProduct().getName();
                    }
                    return reviewMapper.toResponse(review);
                })
                .collect(Collectors.toList());
    }

    /**
     * Permet à l'administrateur de valider un avis client.
     */
    @Transactional // Transactionnel
    public ReviewResponse approveReview(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Avis non trouvé"));
        review.setApproved(true); // Passe à approuvé
        return reviewMapper.toResponse(reviewRepository.save(review)); // Sauvegarde et retourne DTO
    }
}

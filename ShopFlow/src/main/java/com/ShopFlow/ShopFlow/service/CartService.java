package com.ShopFlow.ShopFlow.service;

import com.ShopFlow.ShopFlow.dto.response.CartResponse;
import com.ShopFlow.ShopFlow.entity.*;
import com.ShopFlow.ShopFlow.entity.enums.CouponType;
import com.ShopFlow.ShopFlow.exception.ResourceNotFoundException;
import com.ShopFlow.ShopFlow.mapper.CartMapper;
import com.ShopFlow.ShopFlow.repository.CartRepository;
import com.ShopFlow.ShopFlow.repository.CouponRepository;
import com.ShopFlow.ShopFlow.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Service // Service Spring pour la gestion du panier
@RequiredArgsConstructor // Injection par constructeur
public class CartService {

    private final CartRepository cartRepository; // Accès à la table Cart
    private final ProductRepository productRepository; // Accès à la table Product
    private final CouponRepository couponRepository; // Accès à la table Coupon
    private final CartMapper cartMapper; // Mapping vers les DTOs

    /**
     * Retourne le panier d'un client sous forme de DTO.
     */
    @Transactional(readOnly = true)
    public CartResponse getCartResponse(User customer) {
        // Appelle le mapping sur l'entité récupérée
        return cartMapper.toResponse(getCart(customer));
    }

    /**
     * Récupère ou crée le panier pour un utilisateur donné.
     */
    public Cart getCart(User customer) {
        // Cherche le panier ou initialise un nouveau panier vide si inexistant
        return cartRepository.findByCustomer(customer)
                .orElseGet(() -> cartRepository.save(Cart.builder()
                        .customer(customer) // Propriétaire du panier
                        .items(new ArrayList<>()) // Liste d'articles vide
                        .totalPrice(0.0) // Prix initial à zéro
                        .build()));
    }

    /**
     * Ajoute un produit au panier ou augmente sa quantité.
     */
    @Transactional // Transactionnel pour garantir l'atomicité
    public CartResponse addItemToCart(User customer, Long productId, Integer quantity) {
        Cart cart = getCart(customer); // Récupère le panier
        Product product = productRepository.findById(productId) // Cherche le produit
                .orElseThrow(() -> new ResourceNotFoundException("Produit non trouvé"));

        // Vérifier le stock disponible
        if (product.getStock() <= 0) {
            throw new RuntimeException("Ce produit est en rupture de stock");
        }

        // Recherche si le produit est déjà présent dans les lignes du panier
        CartItem existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElse(null);

        int newTotalQuantity = quantity;
        if (existingItem != null) {
            newTotalQuantity = existingItem.getQuantity() + quantity;
        }

        // Vérifier que la quantité totale ne dépasse pas le stock
        if (newTotalQuantity > product.getStock()) {
            throw new RuntimeException("Stock insuffisant. Stock disponible: " + product.getStock());
        }

        if (existingItem != null) {
            // Si présent, on ajoute la nouvelle quantité à l'existante
            existingItem.setQuantity(newTotalQuantity);
        } else {
            // Si nouveau, on crée une nouvelle ligne CartItem
            CartItem newItem = CartItem.builder()
                    .cart(cart) // Lien vers le panier parent
                    .product(product) // Produit associé
                    .quantity(quantity) // Quantité choisie
                    .build();
            cart.getItems().add(newItem); // Ajout à la liste du panier
        }

        updateCartTotal(cart); // Recalcule le montant total
        return cartMapper.toResponse(cartRepository.save(cart)); // Sauvegarde et retourne le DTO
    }

    /**
     * Modifie la quantité d'un article spécifique.
     */
    @Transactional // Transactionnel
    public CartResponse updateItemQuantity(User customer, Long itemId, Integer quantity) {
        Cart cart = getCart(customer); // Récupère le panier
        // Trouve l'article par son ID dans la liste du panier
        CartItem item = cart.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Article non trouvé dans le panier"));

        item.setQuantity(quantity); // Applique la nouvelle quantité
        updateCartTotal(cart); // Recalcule le montant total
        return cartMapper.toResponse(cartRepository.save(cart)); // Sauvegarde et retourne le DTO
    }

    /**
     * Supprime un article du panier.
     */
    @Transactional // Transactionnel
    public CartResponse removeItemFromCart(User customer, Long itemId) {
        Cart cart = getCart(customer); // Récupère le panier
        // Supprime l'article correspondant à l'ID de la liste
        cart.getItems().removeIf(item -> item.getId().equals(itemId));
        updateCartTotal(cart); // Recalcule le montant total
        return cartMapper.toResponse(cartRepository.save(cart)); // Sauvegarde et retourne le DTO
    }

    /**
     * Applique un coupon de réduction au panier.
     */
    @Transactional // Transactionnel
    public CartResponse applyCoupon(User customer, String code) {
        Cart cart = getCart(customer); // Récupère le panier
        // Recherche le coupon par son code
        Coupon coupon = couponRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon invalide"));

        // Vérifie si le coupon est actif et non expiré
        if (!coupon.isActive() || (coupon.getExpiryDate() != null && coupon.getExpiryDate().isBefore(LocalDateTime.now()))) {
            throw new RuntimeException("Coupon expiré ou inactif");
        }

        cart.setAppliedCoupon(coupon); // Associe le coupon au panier
        updateCartTotal(cart); // Recalcule le total avec la remise
        return cartMapper.toResponse(cartRepository.save(cart)); // Sauvegarde et retourne le DTO
    }

    /**
     * Retire le coupon appliqué au panier.
     */
    @Transactional // Transactionnel
    public CartResponse removeCoupon(User customer) {
        Cart cart = getCart(customer); // Récupère le panier
        cart.setAppliedCoupon(null); // Supprime le lien vers le coupon
        updateCartTotal(cart); // Recalcule le total sans remise
        return cartMapper.toResponse(cartRepository.save(cart)); // Sauvegarde et retourne le DTO
    }

    /**
     * Vide complètement le panier d'un client.
     */
    @Transactional
    public void clearCart(User customer) {
        Cart cart = getCart(customer);
        cart.getItems().clear();
        cart.setAppliedCoupon(null);
        cart.setTotalPrice(0.0);
        cartRepository.save(cart);
    }

    /**
     * Calcule le montant total du panier en tenant compte des prix et des remises.
     */
    private void updateCartTotal(Cart cart) {
        // Somme des prix totaux de chaque ligne (quantité * prix unitaire)
        double total = cart.getItems().stream()
                .mapToDouble(CartItem::getTotalPrice)
                .sum();
        
        // Si un coupon est appliqué, on calcule la remise
        if (cart.getAppliedCoupon() != null) {
            Coupon coupon = cart.getAppliedCoupon();
            if (coupon.getType() == CouponType.PERCENT) {
                // Remise en pourcentage (ex: -20%)
                total = total * (1 - coupon.getValue() / 100);
            } else {
                // Remise fixe (ex: -10€)
                total = Math.max(0, total - coupon.getValue());
            }
        }
        cart.setTotalPrice(total); // Met à jour le champ totalPrice de l'entité
    }
}

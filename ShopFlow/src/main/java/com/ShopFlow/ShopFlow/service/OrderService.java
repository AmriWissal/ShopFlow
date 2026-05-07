package com.ShopFlow.ShopFlow.service;

import com.ShopFlow.ShopFlow.dto.request.OrderRequest;
import com.ShopFlow.ShopFlow.dto.response.OrderResponse;
import com.ShopFlow.ShopFlow.entity.*;
import com.ShopFlow.ShopFlow.entity.enums.OrderStatus;
import com.ShopFlow.ShopFlow.exception.ResourceNotFoundException;
import com.ShopFlow.ShopFlow.mapper.OrderMapper;
import com.ShopFlow.ShopFlow.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service // Service métier pour les commandes
@RequiredArgsConstructor // Injection par constructeur
public class OrderService {

    private final OrderRepository orderRepository; // Accès aux commandes
    private final CartService cartService; // Utilisation du service panier
    private final OrderMapper orderMapper; // Mapping DTO
    private final CouponService couponService; // Service pour les coupons

    /**
     * Transforme le panier actuel en une commande ferme.
     */
    @Transactional // Transactionnel
    public OrderResponse placeOrder(User customer, OrderRequest request) {
        try {
            Cart cart = cartService.getCart(customer); // Récupère le panier du client
            if (cart == null) {
                throw new RuntimeException("Panier introuvable pour le client");
            }
            if (cart.getItems() == null || cart.getItems().isEmpty()) {
                throw new RuntimeException("Impossible de passer une commande avec un panier vide");
            }

        // Calculer le sous-total à partir des items (sans réduction du panier)
        double subTotal = cart.getItems().stream()
                .mapToDouble(CartItem::getTotalPrice)
                .sum();
        
        double discount = 0.0; // Réduction initiale
        Coupon appliedCoupon = null;
        
        // Appliquer le coupon si fourni
        if (request.getCouponCode() != null && !request.getCouponCode().isEmpty()) {
            try {
                // Valider le coupon et récupérer l'entité
                appliedCoupon = couponService.validateAndGetCoupon(request.getCouponCode());
                
                // Calculer la réduction
                if (appliedCoupon.getType().name().equals("PERCENT")) {
                    discount = (subTotal * appliedCoupon.getValue()) / 100.0;
                } else { // FIXED
                    discount = appliedCoupon.getValue();
                }
                
                // S'assurer que la réduction ne dépasse pas le sous-total
                if (discount > subTotal) {
                    discount = subTotal;
                }
                
                // Incrémenter l'utilisation du coupon
                couponService.incrementCouponUsage(request.getCouponCode());
                
            } catch (Exception e) {
                // Si le coupon est invalide, continuer sans réduction
                discount = 0.0;
                appliedCoupon = null;
            }
        }

        double shippingFees = 0.0; // Livraison gratuite
        double totalTTC = subTotal - discount + shippingFees; // Total final avec réduction

        // Crée l'objet de commande à partir du panier
        Order order = Order.builder()
                .customer(customer) // Client propriétaire
                .orderNumber("ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase()) // Numéro unique
                .shippingAddress(request.getShippingAddress()) // Adresse de livraison
                .status(OrderStatus.PENDING) // Statut initial "En attente"
                .subTotal(subTotal) // Montant hors frais
                .discountAmount(discount) // Réduction appliquée
                .shippingFees(shippingFees) // Frais fixes
                .totalTTC(totalTTC) // Total final
                .appliedCoupon(appliedCoupon) // Coupon utilisé
                .build();

        // Convertit chaque article du panier en ligne de commande (OrderItem)
        List<OrderItem> orderItems = cart.getItems().stream()
                .map(cartItem -> {
                    Product product = cartItem.getProduct();
                    
                    // Vérifier le stock disponible
                    if (product.getStock() < cartItem.getQuantity()) {
                        throw new RuntimeException("Stock insuffisant pour le produit: " + product.getName() + 
                                ". Stock disponible: " + product.getStock());
                    }
                    
                    // Décrémenter le stock
                    product.setStock(product.getStock() - cartItem.getQuantity());
                    
                    // Utiliser le prix promotionnel si disponible, sinon le prix de base
                    double unitPrice = (product.getPromoPrice() != null) 
                            ? product.getPromoPrice() 
                            : product.getPrice();
                    
                    return OrderItem.builder()
                        .order(order) // Lien vers la commande parent
                        .product(product) // Produit
                        .variant(cartItem.getVariant()) // Variant (si présent)
                        .quantity(cartItem.getQuantity()) // Quantité choixie
                        .unitPrice(unitPrice) // Prix réel au moment de l'achat
                        .totalPrice(unitPrice * cartItem.getQuantity()) // Total de la ligne
                        .build();
                })
                .collect(Collectors.toList());

        order.setOrderItems(orderItems); // Associe les lignes à la commande
        
        // Sauvegarde de la commande
        Order savedOrder;
        try {
            savedOrder = orderRepository.save(order);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la sauvegarde de la commande: " + e.getMessage(), e);
        }
        
        // ✅ Vider le panier après la création réussie de la commande
        cartService.clearCart(customer);
        
        // Convertir en DTO
        try {
            return orderMapper.toResponse(savedOrder); // Retourne le DTO
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la conversion de la commande en DTO: " + e.getMessage(), e);
        }
    } catch (RuntimeException e) {
        // Relancer les exceptions métier
        throw e;
    } catch (Exception e) {
        // Capturer toute autre exception
        throw new RuntimeException("Erreur inattendue lors de la création de la commande: " + e.getMessage(), e);
    }
    }

    /**
     * Récupère la liste des commandes d'un client spécifique.
     */
    @Transactional(readOnly = true)
    public List<OrderResponse> getMyOrders(User customer) {
        // Recherche par client et tri par date décroissante
        return orderRepository.findByCustomerOrderByOrderDateDesc(customer).stream()
                .map(orderMapper::toResponse) // Conversion en DTO
                .collect(Collectors.toList());
    }

    /**
     * Retourne les détails d'une commande sous forme de DTO.
     */
    @Transactional(readOnly = true)
    public OrderResponse getOrderResponseById(Long id) {
        return orderMapper.toResponse(getOrderById(id)); // Récupère et convertit
    }

    /**
     * Recherche une commande par son ID technique.
     */
    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Commande non trouvée"));
    }

    /**
     * Permet aux vendeurs ou admins de changer l'état d'avancement d'une commande.
     */
    @Transactional // Transactionnel
    public OrderResponse updateOrderStatus(Long id, OrderStatus status) {
        Order order = getOrderById(id); // Récupère la commande
        order.setStatus(status); // Modifie le statut
        return orderMapper.toResponse(orderRepository.save(order)); // Sauvegarde et retourne le DTO
    }

    /**
     * Permet au client d'annuler sa commande si elle est toujours en attente (PENDING).
     */
    @Transactional // Transactionnel
    public OrderResponse cancelOrder(Long id, User customer) {
        Order order = getOrderById(id); // Récupère la commande
        // Vérifie que c'est bien la commande du client connecté
        if (!order.getCustomer().getId().equals(customer.getId())) {
            throw new AccessDeniedException("Vous ne pouvez pas annuler cette commande");
        }
        // L'annulation n'est possible qu'en statut "En attente"
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new RuntimeException("La commande ne peut plus être annulée");
        }
        order.setStatus(OrderStatus.CANCELLED); // Change le statut à "Annulée"
        return orderMapper.toResponse(orderRepository.save(order)); // Sauvegarde et retourne le DTO
    }

    /**
     * Liste toutes les commandes du système pour l'administrateur.
     * Triées par date décroissante (la plus récente en premier).
     */
    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAllByOrderByOrderDateDesc().stream()
                .map(orderMapper::toResponse) // Conversion en DTO
                .collect(Collectors.toList());
    }

    /**
     * Récupère les commandes contenant des produits du vendeur connecté.
     * Triées par date décroissante (la plus récente en premier).
     */
    @Transactional(readOnly = true)
    public List<OrderResponse> getSellerOrders(User seller) {
        // Récupère toutes les commandes qui contiennent au moins un produit du vendeur
        return orderRepository.findAllByOrderByOrderDateDesc().stream()
                .filter(order -> order.getOrderItems().stream()
                        .anyMatch(item -> item.getProduct().getSeller().getId().equals(seller.getId())))
                .map(orderMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Confirme une commande (SELLER ou ADMIN).
     * Vérifie que le stock est suffisant avant de confirmer.
     */
    @Transactional
    public OrderResponse confirmOrder(Long id, User user) {
        // Récupère la commande
        Order order = getOrderById(id);
        
        // Vérifie que la commande est en statut PENDING
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new RuntimeException("Seules les commandes en attente peuvent être confirmées. Statut actuel : " + order.getStatus());
        }
        
        // Si c'est un SELLER, vérifie qu'il a au moins un produit dans la commande
        if (user.getRole().name().equals("SELLER")) {
            boolean hasSellerProduct = order.getOrderItems().stream()
                    .anyMatch(item -> item.getProduct().getSeller().getId().equals(user.getId()));
            
            if (!hasSellerProduct) {
                throw new AccessDeniedException("Vous ne pouvez pas confirmer cette commande car elle ne contient aucun de vos produits");
            }
        }
        
        // ✅ VALIDATION DU STOCK : Vérifier que tous les produits ont un stock suffisant
        StringBuilder stockErrors = new StringBuilder();
        for (OrderItem item : order.getOrderItems()) {
            Product product = item.getProduct();
            int quantityOrdered = item.getQuantity();
            int currentStock = product.getStock();
            
            // Si le stock actuel est insuffisant
            if (currentStock < quantityOrdered) {
                stockErrors.append("- ")
                          .append(product.getName())
                          .append(" : stock insuffisant (commandé: ")
                          .append(quantityOrdered)
                          .append(", disponible: ")
                          .append(currentStock)
                          .append(")\n");
            }
        }
        
        // Si des erreurs de stock ont été détectées, lancer une exception
        if (stockErrors.length() > 0) {
            throw new RuntimeException("Impossible de confirmer la commande. Stock insuffisant pour les produits suivants :\n" + stockErrors.toString());
        }
        
        // Change le statut à CONFIRMED
        order.setStatus(OrderStatus.CONFIRMED);
        return orderMapper.toResponse(orderRepository.save(order));
    }
}


package com.ShopFlow.ShopFlow.service;

import com.ShopFlow.ShopFlow.entity.Order;
import com.ShopFlow.ShopFlow.entity.Product;
import com.ShopFlow.ShopFlow.entity.User;
import com.ShopFlow.ShopFlow.repository.OrderRepository;
import com.ShopFlow.ShopFlow.repository.ProductRepository;
import com.ShopFlow.ShopFlow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service // Service pour les statistiques du tableau de bord
@RequiredArgsConstructor // Injection par constructeur
public class DashboardService {

    private final OrderRepository orderRepository; // Accès aux commandes
    private final ProductRepository productRepository; // Accès aux produits
    private final UserRepository userRepository; // Accès aux utilisateurs

    /**
     * Formate les données graphiques pour s'assurer que les 6 derniers mois sont présents.
     */
    private List<Object[]> formatChartData(List<Object[]> rawData) {
        Map<String, Double> dataMap = new HashMap<>();
        for (Object[] row : rawData) {
            if (row.length >= 2) {
                String monthNum = (String) row[0]; // Format "05"
                Double amount = ((Number) row[1]).doubleValue();
                dataMap.put(monthNum, amount);
            }
        }

        List<Object[]> formattedData = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter numFormatter = DateTimeFormatter.ofPattern("MM");
        DateTimeFormatter nameFormatter = DateTimeFormatter.ofPattern("MMM", Locale.ENGLISH);

        for (int i = 5; i >= 0; i--) {
            LocalDateTime date = now.minusMonths(i);
            String monthNum = date.format(numFormatter);
            String monthName = date.format(nameFormatter);
            formattedData.add(new Object[]{monthName, dataMap.getOrDefault(monthNum, 0.0)});
        }

        return formattedData;
    }

    /**
     * Calcule les statistiques globales pour l'administrateur.
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getAdminStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalOrders", orderRepository.countTotalOrders());
        stats.put("totalProducts", productRepository.count());
        stats.put("totalUsers", userRepository.count());
        
        Double revenue = orderRepository.sumTotalRevenue();
        stats.put("totalRevenue", revenue != null ? revenue : 0.0);
        
        // Commandes récentes (réelles)
        List<Order> allOrders = orderRepository.findAll();
        stats.put("recentOrders", allOrders.stream()
                .sorted((o1, o2) -> o2.getId().compareTo(o1.getId()))
                .limit(5)
                .map(o -> Map.of(
                        "id", o.getOrderNumber() != null ? o.getOrderNumber() : o.getId().toString(),
                        "customer", o.getCustomer() != null ? o.getCustomer().getFullName() : "Anonyme",
                        "total", o.getTotalTTC() != null ? o.getTotalTTC() : 0.0,
                        "status", o.getStatus() != null ? o.getStatus().name() : "PENDING"
                ))
                .toList());

        // Stock faible (réel)
        stats.put("lowStock", productRepository.findAll().stream()
                .filter(p -> p.getStock() != null && p.getStock() < 10 && p.isActive())
                .limit(5)
                .map(p -> Map.of("id", p.getId(), "name", p.getName(), "stock", p.getStock()))
                .toList());

        // Données graphiques (revenus mensuels réels via query, formatés sur 6 mois)
        stats.put("chartData", formatChartData(orderRepository.getMonthlyRevenue()));
        
        // Distribution des produits par catégorie (Pie Chart)
        stats.put("categoryData", productRepository.countProductsByCategory());
        
        // Statut des commandes (Doughnut Chart)
        stats.put("statusData", orderRepository.countOrdersByStatus());

        return stats;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getSellerStats(User seller) {
        Map<String, Object> stats = new HashMap<>();
        List<Product> sellerProducts = productRepository.findBySellerAndActiveTrueWithCategories(seller);
        
        stats.put("myProductsCount", sellerProducts.size());
        stats.put("totalStock", sellerProducts.stream().mapToInt(p -> p.getStock() != null ? p.getStock() : 0).sum());
        
        Double revenue = orderRepository.sumRevenueBySeller(seller);
        stats.put("totalRevenue", revenue != null ? revenue : 0.0);

        // Commandes récentes du vendeur
        stats.put("recentOrders", orderRepository.findAll().stream()
                .filter(o -> o.getOrderItems().stream().anyMatch(oi -> 
                        oi.getProduct() != null && 
                        oi.getProduct().getSeller() != null && 
                        oi.getProduct().getSeller().getId().equals(seller.getId())))
                .sorted((o1, o2) -> o2.getId().compareTo(o1.getId()))
                .limit(5)
                .map(o -> Map.of(
                        "id", o.getOrderNumber() != null ? o.getOrderNumber() : o.getId().toString(),
                        "customer", o.getCustomer() != null ? o.getCustomer().getFullName() : "Anonyme",
                        "total", o.getTotalTTC() != null ? o.getTotalTTC() : 0.0,
                        "status", o.getStatus() != null ? o.getStatus().name() : "PENDING"
                ))
                .toList());

        // Données graphiques du vendeur (formatées sur 6 mois)
        stats.put("chartData", formatChartData(orderRepository.getSellerMonthlyRevenue(seller.getId())));
        
        // Distribution par catégorie pour ce vendeur
        stats.put("categoryData", productRepository.countSellerProductsByCategory(seller));
        
        // Statut des commandes pour ce vendeur
        stats.put("statusData", orderRepository.countSellerOrdersByStatus(seller));

        // Stock faible du vendeur
        stats.put("lowStock", sellerProducts.stream()
                .filter(p -> p.getStock() != null && p.getStock() < 10)
                .limit(5)
                .map(p -> Map.of("id", p.getId(), "name", p.getName(), "stock", p.getStock()))
                .toList());

        return stats;
    }
}

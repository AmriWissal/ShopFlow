package com.ShopFlow.ShopFlow.repository;

import com.ShopFlow.ShopFlow.entity.Order;
import com.ShopFlow.ShopFlow.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @EntityGraph(attributePaths = {"orderItems", "orderItems.product"})
    List<Order> findByCustomerOrderByOrderDateDesc(User customer);

    @EntityGraph(attributePaths = {"orderItems", "orderItems.product", "customer"})
    List<Order> findAll();

    @EntityGraph(attributePaths = {"orderItems", "orderItems.product", "customer"})
    List<Order> findAllByOrderByOrderDateDesc();

    @EntityGraph(attributePaths = {"orderItems", "orderItems.product"})
    Optional<Order> findById(Long id);

    @Query("SELECT COUNT(DISTINCT o) FROM Order o JOIN o.orderItems oi WHERE oi.product.seller = :seller")
    long countBySeller(@Param("seller") User seller);

    @Query("SELECT SUM(oi.totalPrice) FROM Order o JOIN o.orderItems oi WHERE oi.product.seller = :seller")
    Double sumRevenueBySeller(@Param("seller") User seller);

    @Query("SELECT o.status, COUNT(o) FROM Order o GROUP BY o.status")
    List<Object[]> countOrdersByStatus();

    @Query("SELECT o.status, COUNT(o) FROM Order o JOIN o.orderItems oi WHERE oi.product.seller = :seller GROUP BY o.status")
    List<Object[]> countSellerOrdersByStatus(@Param("seller") User seller);

    @Query(value = "SELECT TO_CHAR(o.order_date, 'MM') as month, SUM(o.totalttc) as amount " +
           "FROM orders o " +
           "WHERE o.order_date >= CURRENT_DATE - INTERVAL '6 months' " +
           "GROUP BY TO_CHAR(o.order_date, 'MM'), DATE_TRUNC('month', o.order_date) " +
           "ORDER BY DATE_TRUNC('month', o.order_date)", nativeQuery = true)
    List<Object[]> getMonthlyRevenue();

    @Query(value = "SELECT TO_CHAR(o.order_date, 'MM') as month, SUM(oi.total_price) as amount " +
           "FROM orders o JOIN order_item oi ON oi.order_id = o.id " +
           "JOIN product p ON oi.product_id = p.id " +
           "WHERE p.seller_id = :sellerId " +
           "AND o.order_date >= CURRENT_DATE - INTERVAL '6 months' " +
           "GROUP BY TO_CHAR(o.order_date, 'MM'), DATE_TRUNC('month', o.order_date) " +
           "ORDER BY DATE_TRUNC('month', o.order_date)", nativeQuery = true)
    List<Object[]> getSellerMonthlyRevenue(@Param("sellerId") Long sellerId);

    @Query("SELECT COUNT(o) FROM Order o")
    long countTotalOrders();

    @Query("SELECT SUM(o.totalTTC) FROM Order o")
    Double sumTotalRevenue();
}

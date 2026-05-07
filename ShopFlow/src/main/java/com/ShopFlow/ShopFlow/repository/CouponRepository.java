package com.ShopFlow.ShopFlow.repository;

import com.ShopFlow.ShopFlow.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Interface Repository pour l'entité Coupon.
 * Fournit les méthodes d'accès aux données pour les coupons de réduction.
 */
@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {
    
    /**
     * Recherche un coupon par son code unique.
     */
    Optional<Coupon> findByCode(String code);
}

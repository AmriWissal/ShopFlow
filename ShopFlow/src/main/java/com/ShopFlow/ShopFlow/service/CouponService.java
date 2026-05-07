package com.ShopFlow.ShopFlow.service;

import com.ShopFlow.ShopFlow.dto.request.CouponRequest;
import com.ShopFlow.ShopFlow.dto.response.CouponResponse;
import com.ShopFlow.ShopFlow.entity.Coupon;
import com.ShopFlow.ShopFlow.exception.ResourceNotFoundException;
import com.ShopFlow.ShopFlow.mapper.CouponMapper;
import com.ShopFlow.ShopFlow.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service // Service Spring pour les coupons
@RequiredArgsConstructor // Injection
@Transactional(readOnly = true) // Lecture seule par défaut
public class CouponService {

    private final CouponRepository couponRepository; // Accès coupons
    private final CouponMapper couponMapper; // Mapping DTO

    /**
     * Crée un nouveau coupon en base.
     */
    @Transactional // Écriture
    public CouponResponse createCoupon(CouponRequest request) {
        Coupon coupon = couponMapper.toEntity(request); // DTO -> Entité
        coupon.setCurrentUsage(0); // Usage initial à zéro
        
        // Vérifier si la date d'expiration est dans le passé
        if (request.getExpirationDate() != null && request.getExpirationDate().isBefore(LocalDateTime.now())) {
            coupon.setActive(false); // Marquer comme inactif si expiré
        } else {
            coupon.setActive(true); // Actif par défaut
        }
        
        return couponMapper.toResponse(couponRepository.save(coupon)); // Sauvegarde et retourne DTO
    }

    /**
     * Met à jour les informations d'un coupon.
     */
    @Transactional // Écriture
    public CouponResponse updateCoupon(Long id, CouponRequest request) {
        // Recherche le coupon existant
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon non trouvé"));
        
        // Mise à jour des champs
        coupon.setCode(request.getCode());
        coupon.setType(request.getType());
        coupon.setValue(request.getValue());
        coupon.setExpiryDate(request.getExpirationDate());
        coupon.setMaxUsage(request.getUsageLimit());

        // Vérifier si la date d'expiration est dans le passé
        if (request.getExpirationDate() != null && request.getExpirationDate().isBefore(LocalDateTime.now())) {
            coupon.setActive(false);
        } else {
            coupon.setActive(true);
        }
        
        return couponMapper.toResponse(couponRepository.save(coupon)); // Sauvegarde et retourne DTO
    }

    /**
     * Liste tous les coupons existants.
     */
    @Transactional // Écriture car on peut mettre à jour le statut
    public List<CouponResponse> getAllCoupons() {
        List<Coupon> coupons = couponRepository.findAll();
        LocalDateTime now = LocalDateTime.now();
        
        for (Coupon coupon : coupons) {
            // Vérification dynamique de l'expiration pour l'affichage
            if (coupon.getExpiryDate() != null && coupon.getExpiryDate().isBefore(now) && coupon.isActive()) {
                coupon.setActive(false);
                couponRepository.save(coupon);
            }
        }
        
        return coupons.stream()
                .map(couponMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Vérifie la validité d'un coupon par son code et retourne l'entité.
     */
    public Coupon validateAndGetCoupon(String code) {
        Coupon coupon = couponRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon non trouvé avec le code : " + code));
        
        // Vérifier si le coupon est expiré
        if (coupon.getExpiryDate() != null && coupon.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Ce coupon a expiré");
        }
        
        // Vérifier si le coupon est actif
        if (!coupon.isActive()) {
            throw new RuntimeException("Ce coupon n'est plus actif");
        }
        
        // Vérifier si le coupon a atteint sa limite d'utilisation
        if (coupon.getMaxUsage() != null && coupon.getCurrentUsage() >= coupon.getMaxUsage()) {
            throw new RuntimeException("Ce coupon a atteint sa limite d'utilisation");
        }
        
        return coupon;
    }
    
    /**
     * Vérifie la validité d'un coupon par son code.
     */
    public CouponResponse getCouponByCode(String code) {
        return couponMapper.toResponse(validateAndGetCoupon(code));
    }
    
    /**
     * Incrémente l'utilisation d'un coupon.
     */
    @Transactional
    public void incrementCouponUsage(String code) {
        Coupon coupon = couponRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon non trouvé"));
        coupon.setCurrentUsage(coupon.getCurrentUsage() + 1);
        
        // Désactiver le coupon s'il atteint sa limite
        if (coupon.getMaxUsage() != null && coupon.getCurrentUsage() >= coupon.getMaxUsage()) {
            coupon.setActive(false);
        }
        
        couponRepository.save(coupon);
    }

    /**
     * Récupère un coupon par son ID.
     */
    public CouponResponse getCouponById(Long id) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon non trouvé avec l'ID : " + id));
        return couponMapper.toResponse(coupon);
    }

    /**
     * Active ou désactive un coupon.
     */
    @Transactional // Écriture
    public CouponResponse toggleCouponStatus(Long id) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon non trouvé avec l'ID : " + id));
        
        // Inverser le statut actif/inactif
        coupon.setActive(!coupon.isActive());
        
        return couponMapper.toResponse(couponRepository.save(coupon));
    }

    /**
     * Active un coupon.
     */
    @Transactional // Écriture
    public CouponResponse activateCoupon(Long id) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon non trouvé avec l'ID : " + id));
        
        // Vérifier si le coupon est expiré
        if (coupon.getExpiryDate() != null && coupon.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Impossible d'activer un coupon expiré");
        }
        
        // Vérifier si le coupon a atteint sa limite d'utilisation
        if (coupon.getMaxUsage() != null && coupon.getCurrentUsage() >= coupon.getMaxUsage()) {
            throw new RuntimeException("Impossible d'activer un coupon qui a atteint sa limite d'utilisation");
        }
        
        coupon.setActive(true);
        return couponMapper.toResponse(couponRepository.save(coupon));
    }

    /**
     * Désactive un coupon.
     */
    @Transactional // Écriture
    public CouponResponse deactivateCoupon(Long id) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon non trouvé avec l'ID : " + id));
        
        coupon.setActive(false);
        return couponMapper.toResponse(couponRepository.save(coupon));
    }

    /**
     * Supprime un coupon de la base.
     * Vérifie d'abord que le coupon n'est pas utilisé dans des commandes.
     */
    @Transactional // Écriture
    public void deleteCoupon(Long id) {
        // Vérifier que le coupon existe
        if (!couponRepository.existsById(id)) {
            throw new ResourceNotFoundException("Coupon non trouvé");
        }
        
        // Vérifier si le coupon est utilisé dans des commandes
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon non trouvé"));
        
        // Si le coupon a été utilisé au moins une fois, on ne peut pas le supprimer
        if (coupon.getCurrentUsage() != null && coupon.getCurrentUsage() > 0) {
            throw new RuntimeException("Impossible de supprimer ce coupon car il a déjà été utilisé dans " + 
                    coupon.getCurrentUsage() + " commande(s). Vous pouvez le désactiver à la place.");
        }
        
        // Suppression physique si le coupon n'a jamais été utilisé
        couponRepository.deleteById(id);
    }
}

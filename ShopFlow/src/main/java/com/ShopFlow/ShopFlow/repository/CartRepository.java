package com.ShopFlow.ShopFlow.repository;

import com.ShopFlow.ShopFlow.entity.Cart;
import com.ShopFlow.ShopFlow.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    @EntityGraph(attributePaths = {"items", "items.product"})
    Optional<Cart> findByCustomer(User customer);
}

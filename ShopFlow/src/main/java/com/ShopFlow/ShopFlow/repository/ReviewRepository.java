package com.ShopFlow.ShopFlow.repository;

import com.ShopFlow.ShopFlow.entity.Product;
import com.ShopFlow.ShopFlow.entity.Review;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    @EntityGraph(attributePaths = {"customer"})
    List<Review> findByProductAndApprovedTrue(Product product);
}

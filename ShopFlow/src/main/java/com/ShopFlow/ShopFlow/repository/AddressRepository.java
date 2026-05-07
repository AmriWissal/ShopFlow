package com.ShopFlow.ShopFlow.repository;

import com.ShopFlow.ShopFlow.entity.Address;
import com.ShopFlow.ShopFlow.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByUser(User user);
}

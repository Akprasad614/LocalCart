package com.localcart.repository;

import com.localcart.entity.ShopInventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShopInventoryRepository
        extends JpaRepository<ShopInventory, Long> {

    Optional<ShopInventory> findByShopIdAndProductId(
            Long shopId,
            Long productId
    );
}
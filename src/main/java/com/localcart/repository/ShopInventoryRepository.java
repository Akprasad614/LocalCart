package com.localcart.repository;

import com.localcart.entity.ShopInventory;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ShopInventoryRepository
        extends JpaRepository<ShopInventory, Long> {

    Optional<ShopInventory> findByShopIdAndProductId(
            Long shopId,
            Long productId
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT inventory
            FROM ShopInventory inventory
            WHERE inventory.shop.id = :shopId
            AND inventory.product.id = :productId
            """)
    Optional<ShopInventory> findByShopIdAndProductIdForUpdate(
            @Param("shopId") Long shopId,
            @Param("productId") Long productId
    );

    List<ShopInventory> findByShopId(Long shopId);
}
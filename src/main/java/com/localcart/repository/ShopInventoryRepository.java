package com.localcart.repository;

import com.localcart.entity.ShopInventory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopInventoryRepository
        extends JpaRepository<ShopInventory, Long> {
}
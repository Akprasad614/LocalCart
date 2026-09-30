package com.localcart.repository;

import com.localcart.entity.Shop;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopRepository extends JpaRepository<Shop, Long> {
}
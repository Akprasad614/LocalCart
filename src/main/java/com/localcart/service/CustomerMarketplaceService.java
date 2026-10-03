package com.localcart.service;

import com.localcart.dto.InventoryResponse;
import com.localcart.dto.ShopResponse;
import com.localcart.entity.Shop;
import com.localcart.entity.ShopInventory;
import com.localcart.exception.ResourceNotFoundException;
import com.localcart.repository.ShopInventoryRepository;
import com.localcart.repository.ShopRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerMarketplaceService {

    private final ShopRepository shopRepository;
    private final ShopInventoryRepository inventoryRepository;

    public CustomerMarketplaceService(
            ShopRepository shopRepository,
            ShopInventoryRepository inventoryRepository) {

        this.shopRepository = shopRepository;
        this.inventoryRepository = inventoryRepository;
    }

    public List<ShopResponse> getActiveShops() {

        return shopRepository.findAll()
                .stream()
                .filter(Shop::isActive)
                .map(this::toShopResponse)
                .toList();
    }

    public List<InventoryResponse> getShopProducts(Long shopId) {

        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Shop not found"));

        if (!shop.isActive()) {
            throw new ResourceNotFoundException("Shop is not active");
        }

        return inventoryRepository.findByShopId(shopId)
                .stream()
                .filter(ShopInventory::isAvailable)
                .filter(inventory -> inventory.getStockQuantity() > 0)
                .map(this::toInventoryResponse)
                .toList();
    }

    private ShopResponse toShopResponse(Shop shop) {

        ShopResponse response = new ShopResponse();

        response.setId(shop.getId());
        response.setName(shop.getName());
        response.setOwnerName(shop.getOwnerName());
        response.setPhone(shop.getPhone());
        response.setAddress(shop.getAddress());
        response.setActive(shop.isActive());

        return response;
    }

    private InventoryResponse toInventoryResponse(
            ShopInventory inventory) {

        InventoryResponse response = new InventoryResponse();

        response.setId(inventory.getId());
        response.setShopId(inventory.getShop().getId());
        response.setShopName(inventory.getShop().getName());

        response.setProductId(inventory.getProduct().getId());
        response.setProductName(inventory.getProduct().getName());
        response.setCategory(inventory.getProduct().getCategory());

        response.setPrice(inventory.getPrice());
        response.setStockQuantity(inventory.getStockQuantity());
        response.setAvailable(inventory.isAvailable());

        return response;
    }
}
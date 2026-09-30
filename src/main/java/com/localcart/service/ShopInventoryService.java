package com.localcart.service;

import com.localcart.entity.Product;
import com.localcart.entity.Shop;
import com.localcart.entity.ShopInventory;
import com.localcart.repository.ProductRepository;
import com.localcart.repository.ShopInventoryRepository;
import com.localcart.repository.ShopRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ShopInventoryService {

    private final ShopInventoryRepository shopInventoryRepository;
    private final ShopRepository shopRepository;
    private final ProductRepository productRepository;

    public ShopInventoryService(
            ShopInventoryRepository shopInventoryRepository,
            ShopRepository shopRepository,
            ProductRepository productRepository) {

        this.shopInventoryRepository = shopInventoryRepository;
        this.shopRepository = shopRepository;
        this.productRepository = productRepository;
    }

    public Optional<ShopInventory> addProductToShop(
            Long shopId,
            Long productId,
            double price,
            int stockQuantity) {

        Optional<Shop> shop = shopRepository.findById(shopId);
        Optional<Product> product = productRepository.findById(productId);

        if (shop.isEmpty() || product.isEmpty()) {
            return Optional.empty();
        }

        ShopInventory inventory = new ShopInventory();

        inventory.setShop(shop.get());
        inventory.setProduct(product.get());
        inventory.setPrice(price);
        inventory.setStockQuantity(stockQuantity);
        inventory.setAvailable(stockQuantity > 0);

        return Optional.of(
                shopInventoryRepository.save(inventory)
        );
    }

    public List<ShopInventory> getAllInventory() {
        return shopInventoryRepository.findAll();
    }

    public Optional<ShopInventory> updateInventory(
            Long id,
            double price,
            int stockQuantity) {

        Optional<ShopInventory> existingInventory =
                shopInventoryRepository.findById(id);

        if (existingInventory.isEmpty()) {
            return Optional.empty();
        }

        ShopInventory inventory = existingInventory.get();

        inventory.setPrice(price);
        inventory.setStockQuantity(stockQuantity);
        inventory.setAvailable(stockQuantity > 0);

        return Optional.of(
                shopInventoryRepository.save(inventory)
        );
    }
}
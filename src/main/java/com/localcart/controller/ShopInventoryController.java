package com.localcart.controller;

import com.localcart.entity.ShopInventory;
import com.localcart.service.ShopInventoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/inventory")
public class ShopInventoryController {

    private final ShopInventoryService shopInventoryService;

    public ShopInventoryController(
            ShopInventoryService shopInventoryService) {

        this.shopInventoryService = shopInventoryService;
    }

    @PostMapping
    public ResponseEntity<ShopInventory> addProductToShop(
            @RequestParam Long shopId,
            @RequestParam Long productId,
            @RequestParam double price,
            @RequestParam int stockQuantity) {

        Optional<ShopInventory> inventory =
                shopInventoryService.addProductToShop(
                        shopId,
                        productId,
                        price,
                        stockQuantity
                );

        if (inventory.isPresent()) {
            return ResponseEntity.ok(inventory.get());
        }

        return ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<List<ShopInventory>> getAllInventory() {

        return ResponseEntity.ok(
                shopInventoryService.getAllInventory()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ShopInventory> updateInventory(
            @PathVariable Long id,
            @RequestParam double price,
            @RequestParam int stockQuantity) {

        Optional<ShopInventory> inventory =
                shopInventoryService.updateInventory(
                        id,
                        price,
                        stockQuantity
                );

        if (inventory.isPresent()) {
            return ResponseEntity.ok(inventory.get());
        }

        return ResponseEntity.notFound().build();
    }
}
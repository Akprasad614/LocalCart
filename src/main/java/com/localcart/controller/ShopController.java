package com.localcart.controller;

import com.localcart.entity.Shop;
import com.localcart.service.ShopService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/shops")
public class ShopController {

    private final ShopService shopService;

    public ShopController(ShopService shopService) {
        this.shopService = shopService;
    }

    @PostMapping
    public ResponseEntity<Shop> createShop(
            @RequestBody Shop shop,
            @RequestParam Long ownerId) {

        Optional<Shop> createdShop =
                shopService.createShop(shop, ownerId);

        if (createdShop.isPresent()) {
            return ResponseEntity.ok(createdShop.get());
        }

        return ResponseEntity.badRequest().build();
    }

    @GetMapping
    public ResponseEntity<List<Shop>> getAllShops() {
        return ResponseEntity.ok(shopService.getAllShops());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Shop> getShopById(@PathVariable Long id) {

        Optional<Shop> shop = shopService.getShopById(id);

        if (shop.isPresent()) {
            return ResponseEntity.ok(shop.get());
        }

        return ResponseEntity.notFound().build();
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<Shop>> getShopsByOwner(
            @PathVariable Long ownerId) {

        Optional<List<Shop>> shops =
                shopService.getShopsByOwner(ownerId);

        if (shops.isPresent()) {
            return ResponseEntity.ok(shops.get());
        }

        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Shop> updateShop(
            @PathVariable Long id,
            @RequestBody Shop updatedShop) {

        Optional<Shop> shop =
                shopService.updateShop(id, updatedShop);

        if (shop.isPresent()) {
            return ResponseEntity.ok(shop.get());
        }

        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<Shop> deactivateShop(
            @PathVariable Long id) {

        Optional<Shop> shop =
                shopService.deactivateShop(id);

        if (shop.isPresent()) {
            return ResponseEntity.ok(shop.get());
        }

        return ResponseEntity.notFound().build();
    }
}
package com.localcart.controller;

import com.localcart.entity.Shop;
import com.localcart.service.ShopService;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<Shop> createShop(@RequestBody Shop shop) {

        Shop createdShop = shopService.createShop(shop);

        return new ResponseEntity<>(createdShop, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Shop>> getAllShops() {

        List<Shop> shops = shopService.getAllShops();

        return ResponseEntity.ok(shops);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Shop> getShopById(@PathVariable Long id) {

        Optional<Shop> shop = shopService.getShopById(id);

        if (shop.isPresent()) {
            return ResponseEntity.ok(shop.get());
        }

        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Shop> updateShop(
            @PathVariable Long id,
            @RequestBody Shop shopRequest) {

        Optional<Shop> updatedShop =
                shopService.updateShop(id, shopRequest);

        if (updatedShop.isPresent()) {
            return ResponseEntity.ok(updatedShop.get());
        }

        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<Shop> deactivateShop(@PathVariable Long id) {

        Optional<Shop> shop = shopService.deactivateShop(id);

        if (shop.isPresent()) {
            return ResponseEntity.ok(shop.get());
        }

        return ResponseEntity.notFound().build();
    }
}
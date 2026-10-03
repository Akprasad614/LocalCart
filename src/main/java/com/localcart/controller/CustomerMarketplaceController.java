package com.localcart.controller;

import com.localcart.dto.InventoryResponse;
import com.localcart.dto.ShopResponse;
import com.localcart.service.CustomerMarketplaceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer/marketplace")
public class CustomerMarketplaceController {

    private final CustomerMarketplaceService marketplaceService;

    public CustomerMarketplaceController(
            CustomerMarketplaceService marketplaceService) {

        this.marketplaceService = marketplaceService;
    }

    @GetMapping("/shops")
    public ResponseEntity<List<ShopResponse>> getActiveShops() {

        return ResponseEntity.ok(
                marketplaceService.getActiveShops()
        );
    }

    @GetMapping("/shops/{shopId}/products")
    public ResponseEntity<List<InventoryResponse>> getShopProducts(
            @PathVariable Long shopId) {

        return ResponseEntity.ok(
                marketplaceService.getShopProducts(shopId)
        );
    }
}
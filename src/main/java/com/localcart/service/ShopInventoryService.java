package com.localcart.service;

import com.localcart.dto.AddInventoryRequest;
import com.localcart.dto.InventoryResponse;
import com.localcart.dto.UpdateInventoryRequest;
import com.localcart.entity.Product;
import com.localcart.entity.Role;
import com.localcart.entity.Shop;
import com.localcart.entity.ShopInventory;
import com.localcart.entity.User;
import com.localcart.exception.BadRequestException;
import com.localcart.exception.ResourceNotFoundException;
import com.localcart.repository.ProductRepository;
import com.localcart.repository.ShopInventoryRepository;
import com.localcart.repository.ShopRepository;
import com.localcart.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShopInventoryService {

    private final ShopInventoryRepository inventoryRepository;
    private final ShopRepository shopRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public ShopInventoryService(
            ShopInventoryRepository inventoryRepository,
            ShopRepository shopRepository,
            ProductRepository productRepository,
            UserRepository userRepository) {

        this.inventoryRepository = inventoryRepository;
        this.shopRepository = shopRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    public InventoryResponse addInventory(
            String email,
            Long shopId,
            AddInventoryRequest request) {

        User owner = getShopkeeper(email);

        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Shop not found"));

        checkOwnership(shop, owner);

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Product not found"));

        if (inventoryRepository
                .findByShopIdAndProductId(shopId, request.getProductId())
                .isPresent()) {

            throw new BadRequestException(
                    "Product already exists in this shop inventory");
        }

        ShopInventory inventory = new ShopInventory();

        inventory.setShop(shop);
        inventory.setProduct(product);
        inventory.setPrice(request.getPrice());
        inventory.setStockQuantity(request.getStockQuantity());
        inventory.setAvailable(request.getStockQuantity() > 0);

        return toResponse(inventoryRepository.save(inventory));
    }

    public List<InventoryResponse> getMyInventory(
            String email,
            Long shopId) {

        User owner = getShopkeeper(email);

        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Shop not found"));

        checkOwnership(shop, owner);

        return inventoryRepository.findByShopId(shopId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public InventoryResponse updateInventory(
            String email,
            Long inventoryId,
            UpdateInventoryRequest request) {

        User owner = getShopkeeper(email);

        ShopInventory inventory = inventoryRepository.findById(inventoryId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Inventory not found"));

        checkOwnership(inventory.getShop(), owner);

        inventory.setPrice(request.getPrice());
        inventory.setStockQuantity(request.getStockQuantity());

        if (request.getStockQuantity() == 0) {
            inventory.setAvailable(false);
        } else {
            inventory.setAvailable(request.isAvailable());
        }

        return toResponse(inventoryRepository.save(inventory));
    }

    private User getShopkeeper(String email) {

        if (email == null) {
            throw new BadRequestException("User is not authenticated");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        if (user.getRole() != Role.SHOPKEEPER) {
            throw new BadRequestException(
                    "Only a shopkeeper can perform this action");
        }

        if (!user.isActive()) {
            throw new BadRequestException(
                    "Shopkeeper account is inactive");
        }

        return user;
    }

    private void checkOwnership(Shop shop, User owner) {

        if (shop.getOwner() == null ||
                !shop.getOwner().getId().equals(owner.getId())) {

            throw new BadRequestException(
                    "You do not own this shop");
        }
    }

    private InventoryResponse toResponse(ShopInventory inventory) {

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
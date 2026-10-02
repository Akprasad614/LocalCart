package com.localcart.service;

import com.localcart.entity.Shop;
import com.localcart.entity.User;
import com.localcart.repository.ShopRepository;
import com.localcart.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ShopService {

    private final ShopRepository shopRepository;
    private final UserRepository userRepository;

    public ShopService(ShopRepository shopRepository,
                       UserRepository userRepository) {
        this.shopRepository = shopRepository;
        this.userRepository = userRepository;
    }

    public Optional<Shop> createShop(Shop shop, Long ownerId) {

        Optional<User> owner = userRepository.findById(ownerId);

        if (owner.isEmpty()) {
            return Optional.empty();
        }

        if (!owner.get().isActive()) {
            return Optional.empty();
        }

        shop.setOwner(owner.get());
        shop.setOwnerName(owner.get().getName());
        shop.setActive(true);

        return Optional.of(shopRepository.save(shop));
    }

    public List<Shop> getAllShops() {
        return shopRepository.findAll();
    }

    public Optional<Shop> getShopById(Long id) {
        return shopRepository.findById(id);
    }

    public Optional<List<Shop>> getShopsByOwner(Long ownerId) {

        if (!userRepository.existsById(ownerId)) {
            return Optional.empty();
        }

        return Optional.of(shopRepository.findByOwnerId(ownerId));
    }

    public Optional<Shop> updateShop(Long id, Shop updatedShop) {

        Optional<Shop> existingShop = shopRepository.findById(id);

        if (existingShop.isEmpty()) {
            return Optional.empty();
        }

        Shop shop = existingShop.get();

        shop.setName(updatedShop.getName());
        shop.setPhone(updatedShop.getPhone());
        shop.setAddress(updatedShop.getAddress());

        return Optional.of(shopRepository.save(shop));
    }

    public Optional<Shop> deactivateShop(Long id) {

        Optional<Shop> existingShop = shopRepository.findById(id);

        if (existingShop.isEmpty()) {
            return Optional.empty();
        }

        Shop shop = existingShop.get();
        shop.setActive(false);

        return Optional.of(shopRepository.save(shop));
    }
}
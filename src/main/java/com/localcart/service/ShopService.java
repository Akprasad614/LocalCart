package com.localcart.service;

import com.localcart.entity.Shop;
import com.localcart.repository.ShopRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ShopService {

    private final ShopRepository shopRepository;

    public ShopService(ShopRepository shopRepository) {
        this.shopRepository = shopRepository;
    }

    public Shop createShop(Shop shop) {
        shop.setActive(true);
        return shopRepository.save(shop);
    }

    public List<Shop> getAllShops() {
        return shopRepository.findAll();
    }

    public Optional<Shop> getShopById(Long id) {
        return shopRepository.findById(id);
    }

    public Optional<Shop> updateShop(Long id, Shop shopRequest) {

        Optional<Shop> existingShop = shopRepository.findById(id);

        if (existingShop.isEmpty()) {
            return Optional.empty();
        }

        Shop shop = existingShop.get();

        shop.setName(shopRequest.getName());
        shop.setOwnerName(shopRequest.getOwnerName());
        shop.setPhone(shopRequest.getPhone());
        shop.setAddress(shopRequest.getAddress());

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
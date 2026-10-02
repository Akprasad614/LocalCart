package com.localcart.service;

import com.localcart.dto.CreateOrderRequest;
import com.localcart.dto.OrderItemRequest;
import com.localcart.entity.Order;
import com.localcart.entity.OrderItem;
import com.localcart.entity.OrderStatus;
import com.localcart.entity.Shop;
import com.localcart.entity.ShopInventory;
import com.localcart.entity.User;
import com.localcart.repository.OrderItemRepository;
import com.localcart.repository.OrderRepository;
import com.localcart.repository.ShopInventoryRepository;
import com.localcart.repository.ShopRepository;
import com.localcart.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ShopRepository shopRepository;
    private final ShopInventoryRepository shopInventoryRepository;
    private final UserRepository userRepository;

    public OrderService(
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            ShopRepository shopRepository,
            ShopInventoryRepository shopInventoryRepository,
            UserRepository userRepository) {

        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.shopRepository = shopRepository;
        this.shopInventoryRepository = shopInventoryRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Optional<Order> createOrder(CreateOrderRequest request) {

        Optional<Shop> shopOptional =
                shopRepository.findById(request.getShopId());

        if (shopOptional.isEmpty()) {
            return Optional.empty();
        }

        Optional<User> customerOptional =
                userRepository.findById(request.getCustomerId());

        if (customerOptional.isEmpty()) {
            return Optional.empty();
        }

        Shop shop = shopOptional.get();
        User customer = customerOptional.get();

        if (!shop.isActive() || !customer.isActive()) {
            return Optional.empty();
        }

        for (OrderItemRequest itemRequest : request.getItems()) {

            Optional<ShopInventory> inventoryOptional =
                    shopInventoryRepository.findByShopIdAndProductId(
                            request.getShopId(),
                            itemRequest.getProductId()
                    );

            if (inventoryOptional.isEmpty()) {
                return Optional.empty();
            }

            ShopInventory inventory = inventoryOptional.get();

            if (!inventory.isAvailable()
                    || inventory.getStockQuantity()
                    < itemRequest.getQuantity()) {

                return Optional.empty();
            }
        }

        Order order = new Order();

        order.setShop(shop);
        order.setCustomer(customer);
        order.setStatus(OrderStatus.PLACED);
        order.setDeliveryMode(request.getDeliveryMode());

        order = orderRepository.save(order);

        double totalAmount = 0;

        for (OrderItemRequest itemRequest : request.getItems()) {

            ShopInventory inventory =
                    shopInventoryRepository
                            .findByShopIdAndProductId(
                                    request.getShopId(),
                                    itemRequest.getProductId()
                            )
                            .get();

            double price = inventory.getPrice();

            OrderItem orderItem = new OrderItem();

            orderItem.setOrder(order);
            orderItem.setProduct(inventory.getProduct());
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setPrice(price);

            order.getItems().add(orderItem);

            totalAmount +=
                    price * itemRequest.getQuantity();

            inventory.setStockQuantity(
                    inventory.getStockQuantity()
                            - itemRequest.getQuantity()
            );

            inventory.setAvailable(
                    inventory.getStockQuantity() > 0
            );

            shopInventoryRepository.save(inventory);
            orderItemRepository.save(orderItem);
        }

        order.setTotalAmount(totalAmount);

        return Optional.of(
                orderRepository.save(order)
        );
    }

    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Optional<List<Order>> getOrdersByShop(Long shopId) {

        if (!shopRepository.existsById(shopId)) {
            return Optional.empty();
        }

        return Optional.of(
                orderRepository.findByShopId(shopId)
        );
    }

    public Optional<List<Order>> getOrdersByCustomer(
            Long customerId) {

        if (!userRepository.existsById(customerId)) {
            return Optional.empty();
        }

        return Optional.of(
                orderRepository.findByCustomerId(customerId)
        );
    }

    public Optional<Order> updateOrderStatus(
            Long id,
            OrderStatus newStatus) {

        Optional<Order> existingOrder =
                orderRepository.findById(id);

        if (existingOrder.isEmpty()) {
            return Optional.empty();
        }

        Order order = existingOrder.get();

        if (!isValidStatusChange(
                order.getStatus(),
                newStatus)) {

            return Optional.empty();
        }

        order.setStatus(newStatus);

        return Optional.of(
                orderRepository.save(order)
        );
    }

    private boolean isValidStatusChange(
            OrderStatus currentStatus,
            OrderStatus newStatus) {

        if (currentStatus == OrderStatus.PLACED) {
            return newStatus == OrderStatus.ACCEPTED
                    || newStatus == OrderStatus.CANCELLED;
        }

        if (currentStatus == OrderStatus.ACCEPTED) {
            return newStatus == OrderStatus.PREPARING
                    || newStatus == OrderStatus.CANCELLED;
        }

        if (currentStatus == OrderStatus.PREPARING) {
            return newStatus == OrderStatus.READY
                    || newStatus == OrderStatus.CANCELLED;
        }

        if (currentStatus == OrderStatus.READY) {
            return newStatus == OrderStatus.OUT_FOR_DELIVERY;
        }

        if (currentStatus == OrderStatus.OUT_FOR_DELIVERY) {
            return newStatus == OrderStatus.DELIVERED;
        }

        return false;
    }

    @Transactional
    public Optional<Order> cancelOrder(Long id) {

        Optional<Order> existingOrder =
                orderRepository.findById(id);

        if (existingOrder.isEmpty()) {
            return Optional.empty();
        }

        Order order = existingOrder.get();

        if (order.getStatus() == OrderStatus.DELIVERED
                || order.getStatus() == OrderStatus.CANCELLED) {

            return Optional.empty();
        }

        for (OrderItem item : order.getItems()) {

            Optional<ShopInventory> inventoryOptional =
                    shopInventoryRepository.findByShopIdAndProductId(
                            order.getShop().getId(),
                            item.getProduct().getId()
                    );

            if (inventoryOptional.isPresent()) {

                ShopInventory inventory =
                        inventoryOptional.get();

                inventory.setStockQuantity(
                        inventory.getStockQuantity()
                                + item.getQuantity()
                );

                inventory.setAvailable(true);

                shopInventoryRepository.save(inventory);
            }
        }

        order.setStatus(OrderStatus.CANCELLED);

        return Optional.of(
                orderRepository.save(order)
        );
    }
}
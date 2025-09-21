package com.example.ecommerce.service.impl;

import com.example.ecommerce.mapper.OrderMapper;
import com.example.ecommerce.model.dto.response.OrderResponse;
import com.example.ecommerce.model.entity.*;
import com.example.ecommerce.model.enums.OrderStatus;
import com.example.ecommerce.repository.*;
import com.example.ecommerce.repository.CartItemRepository;
import com.example.ecommerce.repository.OrderItemRepository;
import com.example.ecommerce.service.CartService;
import com.example.ecommerce.service.OrderService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

     private final OrderRepository orderRepository;
     private final CartRepository cartRepository;
     private final ProductRepository productRepository;
     private final OrderItemRepository orderItemRepository;
     private final CartItemRepository cartItemRepository;
     private final UserRepository userRepository;
     private final CartService cartService;

    @Transactional
    @Override
    public OrderResponse createOrderFromCart(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));
        Cart cart = cartRepository.findByUser_Email(email)
                .orElseThrow(() -> new EntityNotFoundException("Cart not found for user: " + email));

        if (cart.getCartItems().isEmpty()) {
            throw new IllegalStateException("Cannot create an order from an empty cart.");
        }

        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);
        order.setOrderItems(new HashSet<>());

        BigDecimal totalPrice = BigDecimal.ZERO;

        Set<OrderItem> orderItems = new HashSet<>();
        for (CartItem cartItem : cart.getCartItems()) {
            Product product = cartItem.getProduct();

            if (product.getStockQuantity() < cartItem.getQuantity()) {
                throw new IllegalStateException("Not enough stock for product: " + product.getName());
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(product.getPrice());
            orderItems.add(orderItem);

            totalPrice = totalPrice.add(product.getPrice().multiply(new BigDecimal(cartItem.getQuantity())));

            product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());
            productRepository.save(product);
        }

        order.setOrderItems(orderItems);
        order.setTotalPrice(totalPrice);

        Order savedOrder = orderRepository.save(order);

        cartService.clearCart(email);

        return OrderMapper.toOrderResponse(savedOrder);
    }

    @Override
    public List<OrderResponse> getUserOrders(String email) {
        return orderRepository.findByUserEmail(email).stream()
                .map(OrderMapper::toOrderResponse)
                .toList();
    }

    @Override
    public List<OrderResponse> listAll() {
        return orderRepository.findAll().stream().map(OrderMapper::toOrderResponse).toList();
    }
}
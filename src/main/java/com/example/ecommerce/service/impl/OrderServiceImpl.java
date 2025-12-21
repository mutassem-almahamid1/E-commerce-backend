package com.example.ecommerce.service.impl;

import com.example.ecommerce.exception.excptions.InsufficientStockException;
import com.example.ecommerce.exception.excptions.CartEmptyException;
import com.example.ecommerce.exception.excptions.CustomException;
import com.example.ecommerce.exception.excptions.ResourceNotFoundException;
import com.example.ecommerce.mapper.OrderMapper;
import com.example.ecommerce.model.dto.response.OrderResponse;
import com.example.ecommerce.model.entity.*;
import com.example.ecommerce.model.enums.OrderStatus;
import com.example.ecommerce.repository.*;
import com.example.ecommerce.service.CartService;
import com.example.ecommerce.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;

@Slf4j
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
    private final ProductServiceImpl productService;

    @Transactional
    @Override
    public OrderResponse createOrderFromCart(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->  new ResourceNotFoundException("User", "email", email));

        Cart cart = cartRepository.findByUser_Email(email)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "user email", email));

        if (cart.getCartItems() == null || cart.getCartItems().isEmpty()) {
            throw new CartEmptyException("cannot create order from an empty cart.");
        }

        validateAndReserveStock(cart);

        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);
        order.setOrderItems(new java.util.HashSet<>());

        BigDecimal totalPrice = BigDecimal.ZERO;

        try {
            for (CartItem cartItem : cart.getCartItems()) {
                if (cartItem == null) {
                    continue;
                }
                Product product = cartItem.getProduct();
                if (product == null) {
                    throw new ResourceNotFoundException("Product", "cartItem", cartItem.getId());
                }
                Integer qtyObj = cartItem.getQuantity();
                int quantity = (qtyObj == null ? 0 : qtyObj);
                if (quantity <= 0) {
                    throw new com.example.ecommerce.exception.excptions.BadRequestException("Invalid quantity for product: " + product.getId());
                }

                if (product.getPrice() == null) {
                    throw new com.example.ecommerce.exception.excptions.BadRequestException("Product price is not set for product: " + product.getId());
                }

                productService.reduceStock(product.getId(), quantity);

                OrderItem orderItem = new OrderItem();
                orderItem.setOrder(order);
                orderItem.setProduct(product);
                orderItem.setQuantity(quantity);
                orderItem.setPrice(product.getPrice());

                order.getOrderItems().add(orderItem);
                totalPrice = totalPrice.add(product.getPrice().multiply(BigDecimal.valueOf(quantity)));

            }

            order.setTotalPrice(totalPrice);
            Order savedOrder = orderRepository.save(order);

            cart.getCartItems().clear();
            cartRepository.save(cart);


            return OrderMapper.toOrderResponse(savedOrder);

        } catch (Exception e) {
            restoreStockOnFailure(order);
            throw e;
        }
    }


    private void validateAndReserveStock(Cart cart) {

        for (CartItem cartItem : cart.getCartItems()) {
            if (cartItem == null) continue;
            Product product = cartItem.getProduct();
            if (product == null) {
                throw new ResourceNotFoundException("Product", "cartItem", cartItem.getId());
            }
            Integer qtyObj = cartItem.getQuantity();
            int requestedQuantity = (qtyObj == null ? 0 : qtyObj);
            if (requestedQuantity <= 0) {
                throw new com.example.ecommerce.exception.excptions.BadRequestException("Invalid quantity for product: " + product.getId());
            }

            Product freshProduct = productRepository.findById(product.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "id", product.getId()));

            if (freshProduct.getStockQuantity() < requestedQuantity) {
                throw new InsufficientStockException(freshProduct.getName(), requestedQuantity, freshProduct.getStockQuantity());
            }
        }

    }

    private void restoreStockOnFailure(Order order) {
        if (order.getOrderItems() != null) {
            for (OrderItem orderItem : order.getOrderItems()) {
                try {
                    productService.restoreStock(orderItem.getProduct().getId(), orderItem.getQuantity());
                } catch (Exception e) {
                    throw e;
                }
            }
        }
    }

    @Transactional
    @Override
    public OrderResponse cancelOrder(Long orderId, String email) {
        log.info("Cancelling order: {} for user: {}", orderId, email);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        if (!order.getUser().getEmail().equals(email)) {
            throw new ResourceNotFoundException("Order", "id", orderId);
        }

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new CustomException("Only pending orders can be cancelled.", HttpStatus.MULTI_STATUS, "Only pending orders can be cancelled.");
        }

        for (OrderItem orderItem : order.getOrderItems()) {
            productService.restoreStock(orderItem.getProduct().getId(), orderItem.getQuantity());
        }

        order.setStatus(OrderStatus.CANCELLED);
        Order savedOrder = orderRepository.save(order);

        return OrderMapper.toOrderResponse(savedOrder);
    }

    @Override
    public List<OrderResponse> getUserOrders(String email) {
        return orderRepository.findByUser_Email(email).stream()
                .map(OrderMapper::toOrderResponse)
                .toList();
    }

    @Override
    public List<OrderResponse> listAll() {
        return orderRepository.findAll().stream().map(OrderMapper::toOrderResponse).toList();
    }
}

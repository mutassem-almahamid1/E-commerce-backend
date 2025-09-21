package com.example.ecommerce.controller;

import com.example.ecommerce.model.dto.response.OrderResponse;
import com.example.ecommerce.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {
    private final OrderService orderService;


    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or #email == authentication.name")
    public ResponseEntity<OrderResponse> create(@RequestParam String email) {
        return ResponseEntity.ok(orderService.createOrderFromCart(email));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or #email == authentication.name")
    public ResponseEntity<List<OrderResponse>> myOrders(@RequestParam String email) {
        return ResponseEntity.ok(orderService.getUserOrders(email));
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OrderResponse>> allOrders() {
        return ResponseEntity.ok(orderService.listAll());
    }
}

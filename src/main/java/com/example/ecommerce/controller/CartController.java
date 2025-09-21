package com.example.ecommerce.controller;

import com.example.ecommerce.model.dto.request.CartItemRequest;
import com.example.ecommerce.model.dto.response.CartResponse;
import com.example.ecommerce.service.CartService;
import com.example.ecommerce.util.MessageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or #email == authentication.name")
    public ResponseEntity<CartResponse> getCartByEmail(@RequestParam String email) {
        return ResponseEntity.ok(cartService.getCartByEmail(email));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CartResponse> getCartById(@PathVariable Long id) {
        return ResponseEntity.ok(cartService.getCartByUserId(id));
    }

    @PostMapping("/items")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CartResponse> addItem(@RequestParam Long id, @Valid @RequestBody CartItemRequest req) {
        return ResponseEntity.ok(cartService.addItemToCart(id, req));
    }

    @PutMapping("/items/{productId}")
    @PreAuthorize("hasRole('ADMIN') or #email == authentication.name")
    public ResponseEntity<CartResponse> updateItem(@RequestParam String email,
                                              @PathVariable Long productId,
                                              @Valid @RequestBody CartItemRequest req) {
        return ResponseEntity.ok(cartService.updateItem(email, productId, req));
    }

    @DeleteMapping("/items/{productId}")
    @PreAuthorize("hasRole('ADMIN') or #email == authentication.name")
    public ResponseEntity<CartResponse> removeItem(@RequestParam String email,
                                                   @PathVariable Long productId) {
        return ResponseEntity.ok(cartService.removeItem(email, productId));
    }

    @DeleteMapping("/items/{cartItemId}/delete")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MessageResponse> deleteCartItem(@PathVariable Long cartItemId) {
        return ResponseEntity.ok(cartService.deleteCartItem(cartItemId));
    }

    @DeleteMapping("/clear")
    @PreAuthorize("hasRole('ADMIN') or #email == authentication.name")
    public ResponseEntity<MessageResponse> clearCart(@RequestParam String email) {
        return ResponseEntity.ok(cartService.clearCart(email));
    }
}

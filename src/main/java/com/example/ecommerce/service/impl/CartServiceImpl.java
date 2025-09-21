package com.example.ecommerce.service.impl;

import com.example.ecommerce.mapper.CartMapper;
import com.example.ecommerce.model.dto.request.CartItemRequest;
import com.example.ecommerce.model.dto.response.CartResponse;
import com.example.ecommerce.model.entity.Cart;
import com.example.ecommerce.model.entity.CartItem;
import com.example.ecommerce.model.entity.Product;
import com.example.ecommerce.model.entity.User;
import com.example.ecommerce.repository.CartItemRepository;
import com.example.ecommerce.repository.CartRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.UserRepository;
import com.example.ecommerce.service.CartService;
import com.example.ecommerce.util.AssistantHelper;
import com.example.ecommerce.util.MessageResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;

    @Override
    public Cart createNewCartById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        Cart newCart = new Cart();
        newCart.setUser(user);
        newCart.setCartItems(new HashSet<>());
        return cartRepository.save(newCart);
    }

    @Override
    public Cart createNewCartByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        Cart newCart = new Cart();
        newCart.setUser(user);
        newCart.setCartItems(new HashSet<>());
        return cartRepository.save(newCart);
    }

    @Override
    @Transactional
    public CartResponse addItemToCart(Long userId, CartItemRequest cartItemRequest) {

        Cart cart = cartRepository.findByUser_Id(userId)
                .orElseGet(() -> createNewCartById(userId));

        Product product = productRepository.findById(cartItemRequest.getProductId())
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));


        Optional<CartItem> existingItem = cart.getCartItems().stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + cartItemRequest.getQuantity());
            cartItemRepository.save(item);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(cartItemRequest.getQuantity());
            cart.getCartItems().add(cartItemRepository.save(newItem));
        }

        return CartMapper.toCartResponse(cart);
    }

    @Override
    public CartResponse getCartByUserId(Long userId) {
        Cart cart = cartRepository.findByUser_Id(userId)
                .orElseGet(() -> createNewCartById(userId));
        return CartMapper.toCartResponse(cart);
    }

    @Override
    public CartResponse getCartByEmail(String email) {
        Cart cart = cartRepository.findByUser_Email(email)
                .orElseGet(() -> createNewCartByEmail(email));
        return CartMapper.toCartResponse(cart);
    }


    @Transactional
    @Override
    public CartResponse updateItem(String email, Long productId, CartItemRequest req) {
        User u = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        Cart cart = cartRepository.findByUser_Id(u.getId())
                .orElseThrow(() -> new EntityNotFoundException("Cart not found"));
        Product p = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));
        CartItem item = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId)
                .orElseThrow(() -> new EntityNotFoundException("Item not in cart"));
        if (p.getStockQuantity() < req.getQuantity()) {
            throw new EntityNotFoundException("Insufficient stock");
        }
        item.setQuantity(req.getQuantity());
        return CartMapper.toCartResponse(cart);
    }


    @Transactional
    @Override
    public MessageResponse deleteCartItem(Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new EntityNotFoundException("Cart item not found"));

        cartItemRepository.delete(cartItem);
        return AssistantHelper.toMessageResponse("Cart item removed successfully");
    }

    @Transactional
    @Override
    public CartResponse removeItem(String email, Long productId) {
        User u = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        Cart cart = cartRepository.findByUser_Id(u.getId())
                .orElseThrow(() -> new EntityNotFoundException("Cart not found"));
        CartItem item = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId)
                .orElseThrow(() -> new EntityNotFoundException("Item not in cart"));
        cart.getCartItems().remove(item);
        cartItemRepository.delete(item);
        return CartMapper.toCartResponse(cart);
    }

    @Transactional
    @Override
    public MessageResponse clearCart(String email) {
        Cart cart = cartRepository.findByUser_Email(email)
                .orElseThrow(() -> new EntityNotFoundException("Cart not found"));
        cart.getCartItems().clear();
        cartRepository.save(cart);
        return AssistantHelper.toMessageResponse("Cart cleared successfully");
    }

}
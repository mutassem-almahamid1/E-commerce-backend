package com.example.ecommerce.service.impl;

import com.example.ecommerce.exception.excptions.CartEmptyException;
import com.example.ecommerce.exception.excptions.InsufficientStockException;
import com.example.ecommerce.exception.excptions.ResourceNotFoundException;
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
    @Transactional
    public Cart createNewCartById(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Cart newCart = new Cart();
        newCart.setUser(user);
        newCart.setCartItems(new HashSet<>());

        Cart savedCart = cartRepository.save(newCart);

        return savedCart;
    }

    @Override
    @Transactional
    public Cart createNewCartByEmail(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        Cart newCart = new Cart();
        newCart.setUser(user);
        newCart.setCartItems(new HashSet<>());

        Cart savedCart = cartRepository.save(newCart);

        return savedCart;
    }

    @Override
    @Transactional
    public CartResponse addItemToCart(Long userId, CartItemRequest cartItemRequest) {

        if (cartItemRequest.getQuantity() <= 0) {
            throw new InsufficientStockException("the quantity must be greater than zero");
        }

        Cart cart = cartRepository.findByUser_Id(userId)
                .orElseGet(() -> createNewCartById(userId));

        Product product = productRepository.findById(cartItemRequest.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", cartItemRequest.getProductId()));

        checkStockAvailability(product, cartItemRequest.getQuantity());

        Optional<CartItem> existingItem = cart.getCartItems().stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            int newQuantity = item.getQuantity() + cartItemRequest.getQuantity();

            checkStockAvailability(product, newQuantity);

            item.setQuantity(newQuantity);
            cartItemRepository.save(item);

        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(cartItemRequest.getQuantity());

            CartItem savedItem = cartItemRepository.save(newItem);
            cart.getCartItems().add(savedItem);

        }

        Cart savedCart = cartRepository.save(cart);
        return CartMapper.toCartResponse(savedCart);
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

        if (req.getQuantity() < 0) {
            throw new InsufficientStockException("the quantity must be zero or greater");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        Cart cart = cartRepository.findByUser_Id(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "user", email));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        CartItem item = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId)
                .orElseThrow(() -> new ResourceNotFoundException("CartItem", "productId", productId));

        if (req.getQuantity() == 0) {
            cart.getCartItems().remove(item);
            cartItemRepository.delete(item);
        } else {
            checkStockAvailability(product, req.getQuantity());

            item.setQuantity(req.getQuantity());
            cartItemRepository.save(item);
        }

        Cart savedCart = cartRepository.save(cart);
        return CartMapper.toCartResponse(savedCart);
    }

    @Transactional
    @Override
    public MessageResponse deleteCartItem(Long cartItemId) {

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("CartItem", "id", cartItemId));

        String productName = cartItem.getProduct().getName();
        cartItemRepository.delete(cartItem);

        return AssistantHelper.toMessageResponse("successful deleted item: " + productName);
    }

    @Transactional
    @Override
    public CartResponse removeItem(String email, Long productId) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        Cart cart = cartRepository.findByUser_Id(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "user", email));

        CartItem item = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId)
                .orElseThrow(() -> new ResourceNotFoundException("CartItem", "productId", productId));

        String productName = item.getProduct().getName();
        cart.getCartItems().remove(item);
        cartItemRepository.delete(item);

        Cart savedCart = cartRepository.save(cart);

        return CartMapper.toCartResponse(savedCart);
    }

    @Transactional
    @Override
    public MessageResponse clearCart(String email) {

        Cart cart = cartRepository.findByUser_Email(email)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "user", email));

        if (cart.getCartItems().isEmpty()) {
            throw new CartEmptyException("the cart is already empty");
        }

        int itemCount = cart.getCartItems().size();
        cart.getCartItems().clear();
        cartRepository.save(cart);

        return AssistantHelper.toMessageResponse("successful cleared cart");
    }



    private void checkStockAvailability(Product product, int requestedQuantity) {
        if (product.getStockQuantity() < requestedQuantity) {
            throw new InsufficientStockException(product.getName(), requestedQuantity, product.getStockQuantity());
        }
    }


    public void validateCartStockAvailability(Cart cart) {

        if (cart.getCartItems().isEmpty()) {
            throw new CartEmptyException("cannot validate stock for an empty cart");
        }

        for (CartItem cartItem : cart.getCartItems()) {
            Product product = cartItem.getProduct();
            int requestedQuantity = cartItem.getQuantity();

            Product freshProduct = productRepository.findById(product.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "id", product.getId()));

            if (freshProduct.getStockQuantity() < requestedQuantity) {
                throw new InsufficientStockException(freshProduct.getName(), requestedQuantity, freshProduct.getStockQuantity());
            }
        }

    }
}


package com.example.ecommerce.service.impl;

import com.example.ecommerce.config.JwtUtil;
import com.example.ecommerce.mapper.UserMapper;
import com.example.ecommerce.model.dto.request.TokenRefreshRequest;
import com.example.ecommerce.model.dto.request.UserRegisterRequest;
import com.example.ecommerce.model.dto.request.UserRequestLogin;
import com.example.ecommerce.model.dto.response.JwtResponse;
import com.example.ecommerce.model.dto.response.UserResponse;
import com.example.ecommerce.model.entity.User;
import com.example.ecommerce.model.enums.Role;
import com.example.ecommerce.repository.UserRepository;
import com.example.ecommerce.service.AuthService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    @Value("${app.jwt.cookie-expiration}")
    private int cookieExpiration;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Transactional
    @Override
    public UserResponse register(UserRegisterRequest req) {
        if (userRepository.findByEmail(req.getEmail()).isPresent()) {
            throw new EntityNotFoundException("Email already registered");
        }
        User u = new User();
        u.setEmail(req.getEmail());
        u.setPassword(passwordEncoder.encode(req.getPassword()));
        u.setRole(Role.USER);
        u.setFullName(req.getFullName());
        u.setImageUrl(req.getImageUrl());
        userRepository.save(u);
        return UserMapper.toUserResponse(u);
    }

    @Transactional
    @Override
    public JwtResponse login(UserRequestLogin req, HttpServletResponse response) {

        User user = userRepository.findByEmail(req.getEmail().trim())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        if (!this.passwordEncoder.matches(req.getPassword().trim(), user.getPassword())) {
            throw new EntityNotFoundException("Incorrect Password.");
        }
        // Generate JWT token
        String jwt = jwtUtil.generateTokenWithRoles(user.getEmail(), List.of(user.getRole().name()));

        // Generate refresh token
        String refreshToken = jwtUtil.generateRefreshTokenFromUsername(user.getEmail());

        // Save refresh token to user
        user.setRefreshToken(refreshToken);
        user.setRefreshTokenExpiryDate(LocalDateTime.now().plusDays(7)); // 7 days expiry
        userRepository.save(user);

        addTokenToHeader(response, jwt, refreshToken);

        return JwtResponse.builder()
                .token(jwt)
                .refreshToken(refreshToken)
                .type("Bearer")
                .id(user.getId().toString())
                .username(user.getFullName())
                .email(user.getEmail())
                .roles(Collections.singletonList(user.getRole().name()))
                .build();
    }






    @Transactional
    @Override
    public void logoutUser(String email, @NotNull HttpServletRequest request, HttpServletResponse response) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // Clear refresh token
        user.setRefreshToken(null);
        user.setRefreshTokenExpiryDate(null);
        userRepository.save(user);

        // Retrieve all cookies from the request
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                // Log cookie details for debugging
                log.info("Clearing cookie: {}", cookie.getName());

                // Set maxAge to 0 to invalidate the cookie
                cookie.setMaxAge(0);
                cookie.setPath("/"); // Ensure the cookie is removed across the entire app
                response.addCookie(cookie);
            }
        } else {
            log.info("No cookies found to clear.");
        }

        // Remove the Authorization header
        response.setHeader(HttpHeaders.AUTHORIZATION, "");
        SecurityContextHolder.clearContext();
    }

    @Override
    public void addTokenToHeader(HttpServletResponse response, String jwtToken, String refreshToken) {
        Cookie cookie = new Cookie("access_token", jwtToken);
        cookie.setMaxAge(cookieExpiration);
        cookie.setPath("/");
        response.addCookie(cookie);

        Cookie refreshCookie = new Cookie("refresh_token", refreshToken);
        refreshCookie.setMaxAge(cookieExpiration);
        refreshCookie.setPath("/");
        response.addCookie(refreshCookie);
        response.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken);
    }

    @Transactional
    @Override
    public JwtResponse refreshToken(TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        // Find user by refresh token
        User user = userRepository.findByRefreshToken(requestRefreshToken)
                .orElseThrow(() -> new BadCredentialsException("Refresh token is not in database!"));

        // Check if refresh token is expired
        if (user.getRefreshTokenExpiryDate().isBefore(LocalDateTime.now())) {
            user.setRefreshToken(null);
            user.setRefreshTokenExpiryDate(null);
            userRepository.save(user);
            throw new BadCredentialsException("Refresh token was expired. Please make a new login request");
        }

        // Generate new JWT token
        String newToken = jwtUtil.generateTokenFromUserName(user.getEmail());

        // Generate new refresh token
        String newRefreshToken = jwtUtil.generateRefreshTokenFromUsername(user.getEmail());

        // Update refresh token in database
        user.setRefreshToken(newRefreshToken);
        user.setRefreshTokenExpiryDate(LocalDateTime.now().plusDays(7)); // 7 days expiry
        userRepository.save(user);

        // Get user roles
        List<String> roles = List.of(user.getRole().name());

        return JwtResponse.builder()
                .token(newToken)
                .refreshToken(newRefreshToken)
                .type("Bearer")
                .id(user.getId().toString())
                .username(user.getFullName())
                .email(user.getEmail())
                .roles(roles)
                .build();
    }
}
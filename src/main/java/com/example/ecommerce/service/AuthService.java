package com.example.ecommerce.service;


import com.example.ecommerce.model.dto.request.TokenRefreshRequest;
import com.example.ecommerce.model.dto.request.UserRegisterRequest;
import com.example.ecommerce.model.dto.request.UserRequestLogin;
import com.example.ecommerce.model.dto.response.JwtResponse;
import com.example.ecommerce.model.dto.response.UserResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import org.springframework.transaction.annotation.Transactional;

public interface AuthService {

    @Transactional
    UserResponse register(UserRegisterRequest req);

    @Transactional
    JwtResponse login(UserRequestLogin req, HttpServletResponse response);

    @Transactional
    void logoutUser(String email, @NotNull HttpServletRequest request, HttpServletResponse response);

    void addTokenToHeader(HttpServletResponse response, String jwtToken, String refreshToken);

    @Transactional
    JwtResponse refreshToken(TokenRefreshRequest request);
}
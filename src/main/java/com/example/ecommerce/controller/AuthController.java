package com.example.ecommerce.controller;

import com.example.ecommerce.model.dto.request.TokenRefreshRequest;
import com.example.ecommerce.model.dto.request.UserRegisterRequest;
import com.example.ecommerce.model.dto.request.UserRequestLogin;
import com.example.ecommerce.model.dto.response.JwtResponse;
import com.example.ecommerce.model.dto.response.UserResponse;
import com.example.ecommerce.service.AuthService;
import com.example.ecommerce.util.AssistantHelper;
import com.example.ecommerce.util.MessageResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @PreAuthorize("permitAll()")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody UserRegisterRequest req) {
        return ResponseEntity.ok(authService.register(req));
    }

    @PostMapping("/login")
    @PreAuthorize("permitAll()")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody UserRequestLogin req,HttpServletResponse httpServletResponse) {
        return ResponseEntity.ok(authService.login(req,httpServletResponse));
    }


    @PostMapping("/refresh-token")
    @PreAuthorize("permitAll()")
    public ResponseEntity<JwtResponse> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        JwtResponse jwtResponse = authService.refreshToken(request);
        return ResponseEntity.ok(jwtResponse);
    }


    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MessageResponse> logoutUser(@NotNull HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        authService.logoutUser(userDetails.getUsername(), request, response);
        return ResponseEntity.ok(AssistantHelper.toMessageResponse("User logged out successfully"));
    }
}
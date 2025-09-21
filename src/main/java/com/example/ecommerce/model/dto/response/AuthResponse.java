package com.example.ecommerce.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String token;
    private String tokenType;

    public static AuthResponse bearer(String token) {
        return new AuthResponse(token, "Bearer");
    }

}

package com.example.ecommerce.model.dto.response;

import com.example.ecommerce.model.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private Long id;
    private String fullName;
    private String imageUrl;
    private String email;
    private Role role;
    private LocalDateTime createdAt;
}

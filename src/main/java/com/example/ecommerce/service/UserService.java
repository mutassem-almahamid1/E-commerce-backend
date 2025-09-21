package com.example.ecommerce.service;

import com.example.ecommerce.model.dto.request.UserRequestUpdate;
import com.example.ecommerce.model.dto.response.UserResponse;
import com.example.ecommerce.util.MessageResponse;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UserService {
    UserResponse getUserById(Long id);
    UserResponse getUserByEmail(String email);
    List<UserResponse> getAllUsers();
    @Transactional
    MessageResponse deleteUser(String email);
    @Transactional
    UserResponse updateUser(String email, UserRequestUpdate userRequestUpdate);
}

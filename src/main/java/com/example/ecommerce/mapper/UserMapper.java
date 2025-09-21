package com.example.ecommerce.mapper;

import com.example.ecommerce.model.dto.request.UserRegisterRequest;
import com.example.ecommerce.model.dto.response.UserResponse;
import com.example.ecommerce.model.entity.User;
import com.example.ecommerce.model.enums.Role;
import com.example.ecommerce.util.AssistantHelper;

import java.time.LocalDateTime;

public class UserMapper {

    public static UserResponse toUserResponse(User user) {
        if (user == null) {
            return null;
        }

        UserResponse userResponse = new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setFullName(user.getFullName());
        userResponse.setEmail(user.getEmail());
        userResponse.setImageUrl(user.getImageUrl());
        userResponse.setRole(user.getRole());
        userResponse.setCreatedAt(user.getCreatedAt());

        return userResponse;
    }

    public static User toUser(UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            return null;
        }

        User user = new User();
        user.setFullName(AssistantHelper.trimString(userRegisterRequest.getFullName()));
        user.setEmail(AssistantHelper.trimString(userRegisterRequest.getEmail()));
        user.setImageUrl(AssistantHelper.trimString(userRegisterRequest.getImageUrl()));
        user.setRole(Role.USER);
        user.setCreatedAt(LocalDateTime.now());

        return user;
    }
}
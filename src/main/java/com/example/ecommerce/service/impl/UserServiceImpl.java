package com.example.ecommerce.service.impl;

import com.example.ecommerce.mapper.UserMapper;
import com.example.ecommerce.model.dto.request.UserRequestUpdate;
import com.example.ecommerce.model.dto.response.UserResponse;
import com.example.ecommerce.model.entity.User;
import com.example.ecommerce.repository.UserRepository;
import com.example.ecommerce.service.UserService;
import com.example.ecommerce.util.AssistantHelper;
import com.example.ecommerce.util.MessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private UserMapper userMapper;

    @Override
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User with id '" + id + "' not found."));

        return UserMapper.toUserResponse(user);
    }

    @Override
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User with email '" + email + "' not found."));

        return UserMapper.toUserResponse(user);
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toUserResponse)
                .toList();
    }

    @Override
    public MessageResponse deleteUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User with email '" + email + "' not found."));

        userRepository.delete(user);

        return AssistantHelper.toMessageResponse("User with email '" + email + "' has been deleted.");
    }

    @Override
    public UserResponse updateUser(String email, UserRequestUpdate userRequestUpdate) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User with email '" + email + "' not found."));

            user.setFullName(userRequestUpdate.getFullName());
            user.setImageUrl(userRequestUpdate.getImageUrl());

        User updatedUser = userRepository.save(user);
        return UserMapper.toUserResponse(updatedUser);
    }
}
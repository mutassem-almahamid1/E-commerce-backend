package com.example.ecommerce.controller;

import com.example.ecommerce.model.dto.request.UserRequestUpdate;
import com.example.ecommerce.model.dto.response.UserResponse;
import com.example.ecommerce.service.UserService;
import com.example.ecommerce.util.MessageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping()
    @PreAuthorize("hasRole('ADMIN') or #email == authentication.name")
    public ResponseEntity<UserResponse> getUserByEmail(@RequestParam String email) {
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }


    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @DeleteMapping()
    @PreAuthorize("hasRole('ADMIN') or #email == authentication.name")
    public ResponseEntity<MessageResponse> deleteUser(@RequestParam String email) {
        return ResponseEntity.ok(userService.deleteUser(email));
    }

    @PatchMapping()
    @PreAuthorize("hasRole('ADMIN') or #email == authentication.name")
    public ResponseEntity<UserResponse> updateUser(@RequestParam String email,
                                                   @Valid @RequestBody  UserRequestUpdate userRequestUpdate) {
        return ResponseEntity.ok(userService.updateUser(email, userRequestUpdate));
    }

}

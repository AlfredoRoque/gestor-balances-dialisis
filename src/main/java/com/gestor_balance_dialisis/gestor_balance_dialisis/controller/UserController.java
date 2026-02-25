package com.gestor_balance_dialisis.gestor_balance_dialisis.controller;

import com.gestor_balance_dialisis.gestor_balance_dialisis.dto.UserDto;
import com.gestor_balance_dialisis.gestor_balance_dialisis.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RequiredArgsConstructor
@RestController
@Tag(name = "User Service", description = "Service for user management, including creation and retrieval of user information.")
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    /**
     * Create a new user in the system.
     *
     * @param user The user data to be created, including username, email, and password.
     * @return The created user with an encrypted password.
     */
    @Operation(
            summary = "Create a new user",
            description = "Create a new user in the system, returns the created user with an encrypted password."
    )
    @PostMapping("/save")
    public ResponseEntity<UserDto> saveUser(@Valid @RequestBody UserDto user) {
        return ResponseEntity.created(URI.create("/api/users/save")).body(userService.save(user));
    }

    /**
     * Update the password of an existing user.
     *
     * @param actualPassword The current password of the user, which will be decrypted and verified before updating.
     * @param newPassword    The new password for the user, which will be decrypted and encrypted before saving.
     * @param userId         The ID of the user whose password is to be updated.
     * @return A response entity with no content, indicating successful password update.
     */
    @Operation(summary = "Update user password",
            description = "Update the password of an existing user, requires the new password and the user ID.")
    @GetMapping("/{userId}/update-password")
    public ResponseEntity<Void> updatePassword(@RequestParam String actualPassword,@RequestParam String newPassword, @PathVariable Long userId) {
        userService.updatePassword(actualPassword,newPassword, userId);
        return ResponseEntity.noContent().build();
    }
}

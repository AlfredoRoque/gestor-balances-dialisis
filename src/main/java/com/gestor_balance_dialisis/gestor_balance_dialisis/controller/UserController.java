package com.gestor_balance_dialisis.gestor_balance_dialisis.controller;

import com.gestor_balance_dialisis.gestor_balance_dialisis.dto.UserDto;
import com.gestor_balance_dialisis.gestor_balance_dialisis.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid user data")
    })
    @PostMapping("/save")
    public UserDto saveUser(@Valid @RequestBody UserDto user) {
        return userService.save(user);
    }
}

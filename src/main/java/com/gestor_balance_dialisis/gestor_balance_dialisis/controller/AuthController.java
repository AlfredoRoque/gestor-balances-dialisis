package com.gestor_balance_dialisis.gestor_balance_dialisis.controller;

import com.gestor_balance_dialisis.gestor_balance_dialisis.dto.JwtResponse;
import com.gestor_balance_dialisis.gestor_balance_dialisis.dto.LoginRequest;
import com.gestor_balance_dialisis.gestor_balance_dialisis.dto.RecoverPasswordRequest;
import com.gestor_balance_dialisis.gestor_balance_dialisis.dto.ValidateMailRequestModel;
import com.gestor_balance_dialisis.gestor_balance_dialisis.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Service for user authentications and validations")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    /**
     * Authenticate a user with username and password, returns a JWT token if successful.
     *
     * @param request The login request containing username and password.
     * @return A response entity containing the JWT token if authentication is successful.
     */
    @Operation(
            summary = "User login",
            description = "Authenticate a user with username and password, returns a JWT token if successful."
    )
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    /**
     * Validate if the email exists in the system, returns true if it exists, otherwise throws an exception.
     *
     * @param validateMailRequestModel The request containing the email to be validated.
     * @return otherwise an exception is thrown.
     */
    @Operation(
            summary = "Validate if email exists",
            description = "Validate if the email exists in the system, returns true if it exists, otherwise throws an exception."
    )
    @PostMapping("/validate/mail")
    public ResponseEntity<Void> validateMail(@Valid @RequestBody ValidateMailRequestModel validateMailRequestModel) {
        authService.validateMail(validateMailRequestModel);
        return ResponseEntity.noContent().build();
    }

    /**
     * Recover the password for a user with the given email, returns true if the email exists, otherwise throws an exception.
     * @param recoverPasswordRequest The request containing the email for which the password recovery process should be initiated.
     */
    @Operation(
            summary = "Recover password",
            description = "Recover password for a user, returns true if the email exists and the recovery process is initiated, otherwise throws an exception."
    )
    @PostMapping("/recover/password")
    public ResponseEntity<Void> recoverPassword(@Valid @RequestBody RecoverPasswordRequest recoverPasswordRequest) throws MessagingException {
        authService.recoverPassword(recoverPasswordRequest);
        return ResponseEntity.noContent().build();
    }
}

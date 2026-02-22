package com.gestor_balance_dialisis.gestor_balance_dialisis.controller;

import com.gestor_balance_dialisis.gestor_balance_dialisis.dto.JwtResponse;
import com.gestor_balance_dialisis.gestor_balance_dialisis.dto.LoginRequest;
import com.gestor_balance_dialisis.gestor_balance_dialisis.security.RsaKeyService;
import com.gestor_balance_dialisis.gestor_balance_dialisis.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Service for user authentications and validations")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final RsaKeyService rsaKeyService;

    /**
     * Returns the RSA public key in Base64 format.
     * The frontend uses this key to encrypt the password before sending it.
     */
    @Operation(summary = "Get RSA public key",
            description = "Returns the server's RSA public key (Base64). Use it in the frontend to encrypt the password before login.")
    @GetMapping("/public-key")
    public ResponseEntity<String> getPublicKey() {
        return ResponseEntity.ok(rsaKeyService.getPublicKeyBase64());
    }

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
     * Invalidate the current user's JWT token by updating the token version, effectively logging out the user from all sessions.
     *
     * @return A response entity with no content, indicating successful logout.
     */
    @Operation(summary = "User logout",
            description = "Invalidate the current user's JWT token by updating the token version, effectively logging out the user from all sessions.")
    @GetMapping("/logout")
    public ResponseEntity<Void> logout() {
        authService.logout();
        return ResponseEntity.noContent().build();
    }

    /**
     * Validate if the email exists in the system, returns true if it exists, otherwise throws an exception.
     *
     * @param email The email to be validated.
     * @return otherwise an exception is thrown.
     */
    @Operation(
            summary = "Validate if email exists",
            description = "Validate if the email exists in the system, returns true if it exists, otherwise throws an exception."
    )
    @GetMapping("/validate/mail")
    public ResponseEntity<Void> validateMail(@RequestParam String email) {
        authService.validateMail(email);
        return ResponseEntity.noContent().build();
    }

    /**
     * Recover the password for a user with the given email, returns true if the email exists, otherwise throws an exception.
     * @param email The email of the user to recover the password for.
     * @return otherwise an exception is thrown.
     */
    @Operation(
            summary = "Recover password",
            description = "Recover password for a user, returns true if the email exists and the recovery process is initiated, otherwise throws an exception."
    )
    @GetMapping("/recover/password")
    public ResponseEntity<Void> recoverPassword(@RequestParam String email) throws MessagingException {
        authService.recoverPassword(email);
        return ResponseEntity.noContent().build();
    }
}

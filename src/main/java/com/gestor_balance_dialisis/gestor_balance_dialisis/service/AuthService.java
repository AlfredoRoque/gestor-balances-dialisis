package com.gestor_balance_dialisis.gestor_balance_dialisis.service;

import com.gestor_balance_dialisis.gestor_balance_dialisis.dto.*;
import com.gestor_balance_dialisis.gestor_balance_dialisis.entity.User;
import com.gestor_balance_dialisis.gestor_balance_dialisis.exception.BalanceGlobalException;
import com.gestor_balance_dialisis.gestor_balance_dialisis.security.JwtUtil;
import com.gestor_balance_dialisis.gestor_balance_dialisis.util.Utility;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service class for handling authentication-related operations such as login, email validation, and password recovery.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {

    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;

    /**
     * Authenticate a user with username and password, returns a JWT token if successful.
     *
     * @param request The login request containing username and password.
     * @return A JwtResponse containing the JWT token if authentication is successful.
     * @throws BalanceGlobalException if the credentials are invalid.
     */
    public JwtResponse login(LoginRequest request) {

        User user = userService.findByUsername(request.getUsername());
        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {
            throw new BalanceGlobalException("Invalid credentials", HttpStatus.CONFLICT.value());
        }
        return new JwtResponse(jwtUtil.generateToken(request.getUsername()));
    }

    /**
     * Validate if the email exists in the system, returns true if it exists, otherwise throws an exception.
     *
     * @param validateMailRequestModel The request containing the email to be validated.
     * @throws BalanceGlobalException if the email does not exist.
     */
    public void validateMail(ValidateMailRequestModel validateMailRequestModel) {
        userService.findByEmail(validateMailRequestModel.getEmail());
    }

    /**
     * Recover the password for a user with the given email, returns true if the email exists, otherwise throws an exception.
     *
     * TODO: Validate migration to kafka for password recovery process, currently it is a simple implementation that sends an email to the user with a temporary password,
     *  but it can be improved by using a more robust and scalable solution like Kafka to handle the password recovery process asynchronously and reliably.
     *
     * @param recoverPasswordRequest The request containing the email to recover the password for.
     * @throws BalanceGlobalException if the email does not exist.
     */
    @Async
    public void recoverPassword(RecoverPasswordRequest recoverPasswordRequest) throws MessagingException {
        User user = userService.findByEmail(recoverPasswordRequest.getEmail());
        String temporaryPassword = Utility.generateTemporaryPassword(10);
        try {
            userService.save(new UserDto(user,temporaryPassword));
        }catch (Exception e){
            throw new BalanceGlobalException("Error al actualizar la contraseña", HttpStatus.CONFLICT.value());
        }
        //create and send email to user with temporary password
        mailService.sendMailToRecoverPassword(user,temporaryPassword);
    }
}

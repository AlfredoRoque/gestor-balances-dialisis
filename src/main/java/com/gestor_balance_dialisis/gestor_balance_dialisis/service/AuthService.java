package com.gestor_balance_dialisis.gestor_balance_dialisis.service;

import com.gestor_balance_dialisis.gestor_balance_dialisis.dto.*;
import com.gestor_balance_dialisis.gestor_balance_dialisis.entity.User;
import com.gestor_balance_dialisis.gestor_balance_dialisis.exception.BalanceGlobalException;
import com.gestor_balance_dialisis.gestor_balance_dialisis.repository.UserRepository;
import com.gestor_balance_dialisis.gestor_balance_dialisis.security.JwtUtil;
import com.gestor_balance_dialisis.gestor_balance_dialisis.security.RsaKeyService;
import com.gestor_balance_dialisis.gestor_balance_dialisis.util.Constants;
import com.gestor_balance_dialisis.gestor_balance_dialisis.util.SecurityUtils;
import com.gestor_balance_dialisis.gestor_balance_dialisis.util.Utility;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Service class for handling authentication-related operations such as login, email validation, and password recovery.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final RsaKeyService rsaKeyService;

    /**
     * Authenticate a user with username and password, returns a JWT token if successful.
     *
     * @param request The login request containing username and password.
     * @return A JwtResponse containing the JWT token if authentication is successful.
     * @throws BalanceGlobalException if the credentials are invalid.
     */
    public JwtResponse login(LoginRequest request) {
        log.info("for user: {}",request.getUsername());
        Optional<User> user = userRepository.findByUsername(request.getUsername());
        user.orElseThrow(() -> new BalanceGlobalException(Constants.USER_NOT_FOUND, HttpStatus.NOT_FOUND.value()));

        String rawPassword = SecurityUtils.decryptPassword(request.getPassword(),rsaKeyService);

        if (!passwordEncoder.matches(rawPassword, user.get().getPassword())) {
            throw new BalanceGlobalException(Constants.INVALID_CREDENTIALS, HttpStatus.CONFLICT.value());
        }
        user.get().setTokenVersion(Long.parseLong(String.valueOf(ThreadLocalRandom.current().nextInt(1, 10_000_001))));
        userRepository.save(user.get());
        return new JwtResponse(jwtUtil.generateToken(request.getUsername(), user.get(), request.getTimeZone()));
    }

    /**
     * Validate if the email exists in the system, returns true if it exists, otherwise throws an exception.
     *
     * @param email The email to be validated.
     * @throws BalanceGlobalException if the email does not exist.
     */
    public void validateMail(String email) {
        log.info("for : {}",email);
        userRepository.findByEmail(email).orElseThrow(() -> new BalanceGlobalException(Constants.INVALID_CREDENTIALS, HttpStatus.NOT_FOUND.value()));;
    }

    /**
     * Recover the password for a user with the given email, returns true if the email exists, otherwise throws an exception.
     *
     * TODO: Validate migration to kafka for password recovery process, currently it is a simple implementation that sends an email to the user with a temporary password,
     *  but it can be improved by using a more robust and scalable solution like Kafka to handle the password recovery process asynchronously and reliably.
     *
     * @param email The email of the user to recover the password for.
     * @throws BalanceGlobalException if the email does not exist.
     */
    @Async
    public void recoverPassword(String email) throws MessagingException {
        log.info("for {}: ",email);
        Optional<User> user = userRepository.findByEmail(email);
        user.orElseThrow(() -> new BalanceGlobalException(Constants.USER_NOT_FOUND, HttpStatus.NOT_FOUND.value()));
        String temporaryPassword = Utility.generateTemporaryPassword(10);
        try {
            user.get().setPassword(passwordEncoder.encode(temporaryPassword));
            userRepository.save(user.get());
        }catch (Exception e){
            throw new BalanceGlobalException(Constants.ERROR_RECOVERING_PASSWORD, HttpStatus.CONFLICT.value());
        }
        //create and send email to user with temporary password
        mailService.sendMailToRecoverPassword(user.get(),temporaryPassword);
    }

    /**
     * Logout a user by invalidating their existing JWT tokens.
     *
     * @throws BalanceGlobalException if the user is not found.
     */
    public void logout() {
        Optional<User> user = userRepository.findById(SecurityUtils.getUserId());
        user.orElseThrow(() -> new BalanceGlobalException(Constants.USER_NOT_FOUND, HttpStatus.NOT_FOUND.value()));
        // Increment the token version to invalidate existing tokens
        user.get().setTokenVersion(user.get().getTokenVersion() + 1);
        userRepository.save(user.get());
    }
}

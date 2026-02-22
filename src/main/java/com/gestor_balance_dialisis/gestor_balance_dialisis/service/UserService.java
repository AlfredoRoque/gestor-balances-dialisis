package com.gestor_balance_dialisis.gestor_balance_dialisis.service;

import com.gestor_balance_dialisis.gestor_balance_dialisis.dto.UserDto;
import com.gestor_balance_dialisis.gestor_balance_dialisis.entity.User;
import com.gestor_balance_dialisis.gestor_balance_dialisis.exception.BalanceGlobalException;
import com.gestor_balance_dialisis.gestor_balance_dialisis.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;

/**
 * Service for managing user-related operations, including finding users by username or email and saving new users with encrypted passwords.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Find a user by their username, returns the user if found, otherwise throws an exception.
     *
     * @param username The username of the user to be found.
     * @return The user with the specified username if found, otherwise an exception is thrown.
     * @throws BalanceGlobalException if the user is not found.
     */
    public User findByUsername(String username) {
        log.info(" user name : {}",username);
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new BalanceGlobalException("User not found",HttpStatus.NOT_FOUND.value()));
    }

    /**
     * Save a new user in the system, returns the saved user with an encrypted password.
     *
     * @param user The user data to be saved, including username, email, and password.
     * @return The saved user with an encrypted password.
     */
    @Transactional
    public UserDto save(UserDto user) {
        log.info("user name : {}",user.getUsername());
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new BalanceGlobalException("Ya existe un usuario asignado al correo con el que intentas registrarte.", HttpStatus.CONFLICT.value());
        } else if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new BalanceGlobalException("Ya existe un usuario con el nombre que intentas registrarte.", HttpStatus.CONFLICT.value());
        }
        return new UserDto(userRepository.save(new User(user,passwordEncoder.encode(user.getPassword()))));
    }

    /**
     * Find a user by their email, returns the user if found, otherwise throws an exception.
     *
     * @param email The email of the user to be found.
     * @throws BalanceGlobalException if the user is not found.
     */
    public User findByEmail(String email) {
        log.info(" user mail : {}",email);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BalanceGlobalException("Usuario no encontrado",HttpStatus.NOT_FOUND.value()));
    }

    /**
     * Update the password of an existing user, returns the updated user with an encrypted password.
     *
     * @param user The user data to be updated, including username, email, and new password.
     */
    @Transactional
    public void updatePassword(UserDto user) {
        log.info(" userName : {}",user.getUsername());
        new UserDto(userRepository.save(new User(user, passwordEncoder.encode(user.getPassword()))));
    }
}

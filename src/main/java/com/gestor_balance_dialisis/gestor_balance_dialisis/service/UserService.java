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
}

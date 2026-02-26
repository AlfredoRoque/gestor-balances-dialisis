package com.gestor_balance_dialisis.gestor_balance_dialisis.service;

import com.gestor_balance_dialisis.gestor_balance_dialisis.dto.UserDto;
import com.gestor_balance_dialisis.gestor_balance_dialisis.entity.User;
import com.gestor_balance_dialisis.gestor_balance_dialisis.exception.BalanceGlobalException;
import com.gestor_balance_dialisis.gestor_balance_dialisis.repository.UserRepository;
import com.gestor_balance_dialisis.gestor_balance_dialisis.security.RsaKeyService;
import com.gestor_balance_dialisis.gestor_balance_dialisis.util.Constants;
import com.gestor_balance_dialisis.gestor_balance_dialisis.util.SecurityUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;

import java.util.Optional;

/**
 * Service for managing user-related operations, including finding users by username or email and saving new users with encrypted passwords.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RsaKeyService rsaKeyService;

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
            throw new BalanceGlobalException(Constants.EMAIL_USER_EXIST, HttpStatus.CONFLICT.value());
        } else if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new BalanceGlobalException(Constants.USER_NAME_EXIST, HttpStatus.CONFLICT.value());
        }
        String rawPassword = SecurityUtils.decryptPassword(user.getPassword(),rsaKeyService);
        return new UserDto(userRepository.save(new User(user,passwordEncoder.encode(rawPassword))));
    }

    /**
     * Update the password of an existing user.
     *
     * @param actualPassword The current password of the user, which will be decrypted and verified before updating.
     * @param newPassword    The new password for the user, which will be decrypted and encrypted before saving.
     * @param userId         The ID of the user whose password is to be updated.
     */
    @Transactional
    public void updatePassword(String actualPassword,String newPassword, Long userId) {
        log.info("userId : {}",userId);
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            String actualRawPassword = SecurityUtils.decryptPassword(actualPassword,rsaKeyService);
            if (!passwordEncoder.matches(actualRawPassword, user.get().getPassword())) {
                throw new BalanceGlobalException(Constants.INVALID_CREDENTIALS, HttpStatus.CONFLICT.value());
            }
            String newRawPassword = SecurityUtils.decryptPassword(newPassword,rsaKeyService);
            User userSave = new User(new UserDto(user.get()),passwordEncoder.encode(newRawPassword));
            userSave.setPatients(user.get().getPatients());
            userSave.setMedicines(user.get().getMedicines());
            userSave.setVitalSigns(user.get().getVitalSigns());
            userRepository.save(userSave);
            return;
        }
        throw new BalanceGlobalException(Constants.UPDATE_ERROR_CREDENTIALS, HttpStatus.CONFLICT.value());
    }
}

package com.gestor_balance_dialisis.gestor_balance_dialisis.security;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;

/**
 * Component that generates an RSA key pair on startup and provides
 * encryption/decryption utilities.
 *
 * <p>The public key is exposed to the frontend so it can encrypt the password
 * before sending it over the wire. The backend uses the private key to decrypt
 * it before validating with BCrypt.</p>
 */
@Slf4j
@Getter
@Component
public class RsaKeyService {

    private RSAPublicKey  publicKey;
    private RSAPrivateKey privateKey;

    /**
     * Initializes the RSA key pair when the application starts.
     * Generates a 2048-bit RSA key pair and stores the public and private keys.
     *
     * @throws Exception if key generation fails.
     */
    @PostConstruct
    public void init() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        KeyPair pair = generator.generateKeyPair();
        publicKey  = (RSAPublicKey)  pair.getPublic();
        privateKey = (RSAPrivateKey) pair.getPrivate();
        log.info("RSA key pair generated successfully.");
    }

    /**
     * Returns the public key encoded in Base64 (SubjectPublicKeyInfo / X.509 format).
     * This is the value that should be sent to the frontend.
     */
    public String getPublicKeyBase64() {
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }

    /**
     * Decrypts a Base64-encoded RSA-encrypted string using the private key.
     *
     * @param encryptedBase64 The Base64-encoded ciphertext produced by the frontend.
     * @return The original plaintext.
     * @throws Exception if decryption fails (wrong key, corrupted payload, etc.).
     */
    public String decrypt(String encryptedBase64) throws Exception {
        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedBase64);
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return new String(cipher.doFinal(encryptedBytes));
    }
}


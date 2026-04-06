package com.gestor_balance_dialisis.gestor_balance_dialisis.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for the RsaKeyService class, which is responsible for generating RSA key pairs and decrypting data encrypted with the public key. These tests verify that the service correctly initializes keys, returns valid Base64-encoded public keys, and properly decrypts valid ciphertext while rejecting invalid input.
 */
class RsaKeyServiceTest {

    private RsaKeyService rsaKeyService;

    /**
     * Set up the RsaKeyService instance before each test. This involves initializing the service, which generates a new RSA key pair. The setup ensures that each test starts with a fresh instance of RsaKeyService with valid keys.
     * @throws Exception if initialization fails, which would indicate a problem with key generation that needs to be addressed before running the tests.
     */
    @BeforeEach
    void setUp() throws Exception {
        rsaKeyService = new RsaKeyService();
        rsaKeyService.init();
    }

    /**
     * Test that init generates both a public and private RSA key pair.
     * Verifies that after initialization, neither key is null.
     */
    @Test
    void init_generatesPublicAndPrivateKeys() {
        assertThat(rsaKeyService.getPublicKey()).isNotNull();
        assertThat(rsaKeyService.getPrivateKey()).isNotNull();
    }

    /**
     * Test that getPublicKeyBase64 returns a non-empty Base64-encoded string.
     * Verifies that the returned string only contains valid Base64 characters
     * (A-Z, a-z, 0-9, +, /, =).
     */
    @Test
    void getPublicKeyBase64_returnsNonEmptyBase64String() {
        String base64 = rsaKeyService.getPublicKeyBase64();
        assertThat(base64).isNotNull().isNotEmpty();
        // Base64 only contains A-Z, a-z, 0-9, +, /, =
        assertThat(base64).matches("[A-Za-z0-9+/=]+");
    }

    /**
     * Test that decrypt correctly recovers the original plaintext from a valid RSA-encrypted input.
     * Verifies the full round-trip: encrypting with the public key and decrypting with the private key
     * returns the original password string.
     */
    @Test
    void decrypt_withValidEncryptedText_returnsOriginalText() throws Exception {
        // Encrypt using the public key manually
        javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(javax.crypto.Cipher.ENCRYPT_MODE, rsaKeyService.getPublicKey());
        byte[] encrypted = cipher.doFinal("my-secret-password".getBytes());
        String encryptedBase64 = java.util.Base64.getEncoder().encodeToString(encrypted);

        String result = rsaKeyService.decrypt(encryptedBase64);

        assertThat(result).isEqualTo("my-secret-password");
    }

    /**
     * Test that decrypt throws an exception when given an invalid Base64 string.
     * Verifies that malformed or non-RSA ciphertext input is properly rejected.
     */
    @Test
    void decrypt_withInvalidBase64_throwsException() {
        assertThatThrownBy(() -> rsaKeyService.decrypt("not-valid-base64!!!"))
                .isInstanceOf(Exception.class);
    }

    /**
     * Test that the generated public key uses the RSA algorithm.
     * Verifies the algorithm property of the public key matches "RSA".
     */
    @Test
    void publicKeyAlgorithm_isRSA() {
        assertThat(rsaKeyService.getPublicKey().getAlgorithm()).isEqualTo("RSA");
    }

    /**
     * Test that the generated private key uses the RSA algorithm.
     * Verifies the algorithm property of the private key matches "RSA".
     */
    @Test
    void privateKeyAlgorithm_isRSA() {
        assertThat(rsaKeyService.getPrivateKey().getAlgorithm()).isEqualTo("RSA");
    }
}

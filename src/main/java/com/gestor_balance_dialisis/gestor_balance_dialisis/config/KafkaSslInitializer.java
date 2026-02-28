package com.gestor_balance_dialisis.gestor_balance_dialisis.config;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.FileOutputStream;
import java.nio.file.Path;
import java.util.Base64;

/**
 * ApplicationContextInitializer that loads Kafka SSL certificates from environment variables, decodes them from Base64, and writes them to temporary files.
 * This allows the application to use the SSL certificates for secure communication with Kafka brokers.
 */
public class KafkaSslInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext context) {
        try {
            String tmpDir = System.getProperty("java.io.tmpdir");

            // Decodifica truststore
            String truststoreBase64 = System.getenv("KAFKA_TRUSTSTORE_BASE64");
            Path truststorePath = Path.of(tmpDir, "client.truststore.jks");
            try (FileOutputStream fos = new FileOutputStream(truststorePath.toFile())) {
                fos.write(Base64.getDecoder().decode(truststoreBase64));
            }
            System.setProperty("ssl.truststore.location", truststorePath.toString());

            // Decodifica keystore
            String keystoreBase64 = System.getenv("KAFKA_KEYSTORE_BASE64");
            Path keystorePath = Path.of(tmpDir, "client.keystore.jks");
            try (FileOutputStream fos = new FileOutputStream(keystorePath.toFile())) {
                fos.write(Base64.getDecoder().decode(keystoreBase64));
            }
            System.setProperty("ssl.keystore.location", keystorePath.toString());

            System.out.println("Kafka SSL files created at: " + tmpDir);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create Kafka SSL files", e);
        }
    }
}

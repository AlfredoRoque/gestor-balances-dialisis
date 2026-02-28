package com.gestor_balance_dialisis.gestor_balance_dialisis.kafka;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.util.Base64;

@Component
public class KafkaCertLoader {

    @Value("${KAFKA_TRUSTSTORE_BASE64}")
    private String truststoreBase64;

    @Value("${KAFKA_KEYSTORE_BASE64}")
    private String keystoreBase64;

    @PostConstruct
    public void loadCerts() throws Exception {

        byte[] truststoreBytes = Base64.getDecoder().decode(truststoreBase64);
        try (FileOutputStream fos = new FileOutputStream("/tmp/client.truststore.jks")) {
            fos.write(truststoreBytes);
        }

        byte[] keystoreBytes = Base64.getDecoder().decode(keystoreBase64);
        try (FileOutputStream fos = new FileOutputStream("/tmp/client.keystore.jks")) {
            fos.write(keystoreBytes);
        }
    }
}
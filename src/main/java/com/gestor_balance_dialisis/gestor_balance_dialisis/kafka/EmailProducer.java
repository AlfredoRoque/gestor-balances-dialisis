package com.gestor_balance_dialisis.gestor_balance_dialisis.kafka;

import com.gestor_balance_dialisis.gestor_balance_dialisis.dto.EmailEventDto;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * Service responsible for producing email events and sending them to a Kafka topic.
 * It uses the KafkaTemplate to send EmailEventDto objects to the "email-topic".
 */
@Service
@RequiredArgsConstructor
public class EmailProducer {

    private final KafkaTemplate<String, EmailEventDto> kafkaTemplate;

    /**
     * Sends an email event to the Kafka topic "email-topic".
     *
     * @param event the EmailEventDto object containing the email event data to be sent
     */
    public void send(EmailEventDto event) {
        kafkaTemplate.send("email-topic", event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        System.out.println("Mensaje enviado correctamente");
                    } else {
                        ex.printStackTrace();
                    }
                });
    }
}

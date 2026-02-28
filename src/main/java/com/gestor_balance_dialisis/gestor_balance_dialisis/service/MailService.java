package com.gestor_balance_dialisis.gestor_balance_dialisis.service;

import com.gestor_balance_dialisis.gestor_balance_dialisis.dto.EmailEventDto;
import com.gestor_balance_dialisis.gestor_balance_dialisis.entity.MailTemplate;
import com.gestor_balance_dialisis.gestor_balance_dialisis.entity.Patient;
import com.gestor_balance_dialisis.gestor_balance_dialisis.entity.User;
import com.gestor_balance_dialisis.gestor_balance_dialisis.exception.BalanceGlobalException;
import com.gestor_balance_dialisis.gestor_balance_dialisis.kafka.EmailProducer;
import com.gestor_balance_dialisis.gestor_balance_dialisis.repository.MailTemplateRepository;
import com.gestor_balance_dialisis.gestor_balance_dialisis.repository.PatientRepository;
import com.gestor_balance_dialisis.gestor_balance_dialisis.util.Constants;
import com.gestor_balance_dialisis.gestor_balance_dialisis.util.SecurityUtils;
import com.gestor_balance_dialisis.gestor_balance_dialisis.util.TEMPLATE_ENUM;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Service responsible for handling email-related operations, such as sending recovery emails and balance reports to users.
 * It utilizes the SendGrid API to send emails and Thymeleaf for email template processing.
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class MailService {

    private final TemplateEngine templateEngine;
    private final PatientRepository patientRepository;
    private final MailTemplateRepository mailTemplateRepository;
    private final EmailProducer emailProducer;

    /**
     * Sends a recovery email to the user with a temporary password.
     *
     * @param user              the user to whom the recovery email will be sent
     * @param temporaryPassword the temporary password to include in the email
     */
    public void sendMailToRecoverPassword(User user, String temporaryPassword) {

        log.info("Publishing recovery mail event for user: {}", user.getUsername());

        Context context = new Context();
        context.setVariable("userName", user.getUsername());
        context.setVariable("password", temporaryPassword);

        publishMailEvent(
                TEMPLATE_ENUM.TEMPLATE_RECOVER_PASSWORD.getValue(),
                context,
                user.getEmail(),
                Constants.RECOVER_PASSWORD_SUBJECT,
                null,
                null
        );
    }

    /**
     * Sends an email to the user with their balance report as a PDF attachment.
     *
     * @param response   the response containing the PDF data and file name
     * @param patientId  the ID of the patient to whom the email will be sent
     * @param startDate  the start date of the balance report period
     * @param endDate    the end date of the balance report period
     */
    public void sendBalancesMailToUserMail(List<Object> response,
                                           Long patientId,
                                           Instant startDate,
                                           Instant endDate) {

        log.info("Publishing balance mail event for patientId: {}", patientId);

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() ->
                        new BalanceGlobalException(
                                Constants.PATIENT_NOT_FOUND + patientId,
                                HttpStatus.CONFLICT.value()));

        Context context = new Context();
        context.setVariable("userName", patient.getUser().getUsername());
        context.setVariable("patientName", patient.getName());

        ZoneId zone = SecurityUtils.getUserZone();
        DateTimeFormatter formatterDay = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        String subject = String.format(
                Constants.BALANCE_FILE_NAME,
                patient.getName(),
                startDate.atZone(zone).format(formatterDay),
                endDate.atZone(zone).format(formatterDay)
        );

        publishMailEvent(
                TEMPLATE_ENUM.TEMPLATE_BALANCE_REPORT.getValue(),
                context,
                patient.getUser().getEmail(),
                subject,
                (byte[]) response.get(0),
                (String) response.get(2)
        );
    }

    /**
     * Helper method to publish an email event to Kafka.
     *
     * @param templateName the name of the email template to use
     * @param context      the Thymeleaf context containing variables for the template
     * @param email        the recipient's email address
     * @param subject      the subject of the email
     * @param pdfBytes     the byte array of the PDF attachment (if any)
     * @param fileName     the name of the PDF attachment (if any)
     */
    private void publishMailEvent(String templateName,
                                  Context context,
                                  String email,
                                  String subject,
                                  byte[] pdfBytes,
                                  String fileName) {

        MailTemplate template = mailTemplateRepository.findByName(templateName);
        String htmlContent = templateEngine.process(template.getContent(), context);

        EmailEventDto event = EmailEventDto.builder()
                .to(email)
                .subject(subject)
                .htmlContent(htmlContent)
                .attachment(pdfBytes)
                .attachmentName(fileName)
                .build();

        emailProducer.send(event);
        log.info("Email event published to Kafka for {}", email);
    }
}
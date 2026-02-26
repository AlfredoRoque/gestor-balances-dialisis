package com.gestor_balance_dialisis.gestor_balance_dialisis.service;

import com.gestor_balance_dialisis.gestor_balance_dialisis.entity.MailTemplate;
import com.gestor_balance_dialisis.gestor_balance_dialisis.entity.Patient;
import com.gestor_balance_dialisis.gestor_balance_dialisis.entity.User;
import com.gestor_balance_dialisis.gestor_balance_dialisis.exception.BalanceGlobalException;
import com.gestor_balance_dialisis.gestor_balance_dialisis.repository.MailTemplateRepository;
import com.gestor_balance_dialisis.gestor_balance_dialisis.repository.PatientRepository;
import com.gestor_balance_dialisis.gestor_balance_dialisis.util.SecurityUtils;
import com.gestor_balance_dialisis.gestor_balance_dialisis.util.TEMPLATE_ENUM;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * Service for sending emails to users, specifically for password recovery purposes.
 * It uses JavaMailSender to send emails and Thymeleaf TemplateEngine to generate email content from templates.
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class MailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final PatientRepository patientRepository;
    private final MailTemplateRepository mailTemplateRepository;

    /**
     * Email the user with a temporary password for password recovery.
     *
     * @param user              The user to whom the email will be sent, containing their email and username.
     * @param temporaryPassword The temporary password to be included in the email content.
     * @throws MessagingException if there is an error while sending the email.
     */
    public void sendMailToRecoverPassword(User user, String temporaryPassword) throws MessagingException {
        log.info(" for user: {}",user.getUsername());

        MailTemplate template = mailTemplateRepository
                .findByName(TEMPLATE_ENUM.TEMPLATE_RECOVER_PASSWORD.getValue());

        Context context = new Context();
        context.setVariable("userName", user.getUsername());
        context.setVariable("password", temporaryPassword);

        String htmlContent = templateEngine.process(template.getContent(), context);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(user.getEmail());
        helper.setSubject("Recuperar contraseña 🚀");
        helper.setText(htmlContent, true);

        mailSender.send(message);
    }

    /**
     * Emails the user with a balance report for a specified date range.
     *
     * @param response  A list containing the PDF report as a byte array, the filename, and the patient's name.
     * @param patientId The ID of the patient to whom the email will be sent.
     * @param startDate The start date of the balance report.
     * @param endDate   The end date of the balance report.
     * @throws MessagingException if there is an error while sending the email.
     * @throws IOException        if there is an error while creating or writing to the temporary file for the PDF attachment.
     */
    public void sendBalancesMailToUserMail(List<Object> response, Long patientId, Instant startDate, Instant endDate) throws MessagingException, IOException {
        log.info(" patientId : {}",patientId);
        Optional<Patient> patient = patientRepository.findById(patientId);
        if(patient.isPresent()) {
            MailTemplate template = mailTemplateRepository
                    .findByName(TEMPLATE_ENUM.TEMPLATE_BALANCE_REPORT.getValue());

            Context context = new Context();
            context.setVariable("userName", patient.get().getUser().getUsername());
            context.setVariable("patientName", patient.get().getName());

            String htmlContent = templateEngine.process(template.getContent(), context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            DateTimeFormatter formatterDay = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            helper.setTo(patient.get().getUser().getEmail());
            ZoneId zone = SecurityUtils.getUserZone();
            helper.setSubject("Balance de "+patient.get().getName() + " de " + startDate.atZone(zone)
                    .format(formatterDay) + " a " + endDate.atZone(zone).format(formatterDay) + " 🚀");
            helper.setText(htmlContent, true);
            File tempFile = File.createTempFile("reporte_", ".pdf");
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                fos.write((byte[]) response.get(0));
            } catch (IOException e) {
                log.error("Error writing the temporary file: {}", String.valueOf(e));
            }
            helper.addAttachment((String) response.get(2),tempFile);

            mailSender.send(message);
            return;
        }
        throw new BalanceGlobalException("Patient with ID was not found: " + patientId, HttpStatus.CONFLICT.value());
    }
}

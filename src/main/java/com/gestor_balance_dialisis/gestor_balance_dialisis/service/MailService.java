package com.gestor_balance_dialisis.gestor_balance_dialisis.service;

import com.gestor_balance_dialisis.gestor_balance_dialisis.entity.MailTemplate;
import com.gestor_balance_dialisis.gestor_balance_dialisis.entity.Patient;
import com.gestor_balance_dialisis.gestor_balance_dialisis.entity.User;
import com.gestor_balance_dialisis.gestor_balance_dialisis.exception.BalanceGlobalException;
import com.gestor_balance_dialisis.gestor_balance_dialisis.repository.MailTemplateRepository;
import com.gestor_balance_dialisis.gestor_balance_dialisis.repository.PatientRepository;
import com.gestor_balance_dialisis.gestor_balance_dialisis.util.Constants;
import com.gestor_balance_dialisis.gestor_balance_dialisis.util.SecurityUtils;
import com.gestor_balance_dialisis.gestor_balance_dialisis.util.TEMPLATE_ENUM;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

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

    @Value("${app.mail.from}")
    private String fromEmail;

    /**
     * Email the user with a temporary password for password recovery.
     *
     * @param user              The user to whom the email will be sent, containing their email and username.
     * @param temporaryPassword The temporary password to be included in the email content.
     * @throws MessagingException if there is an error while sending the email.
     */
    public void sendMailToRecoverPassword(User user, String temporaryPassword) throws MessagingException {
        log.info(" for user: {}",user.getUsername());

        Context context = new Context();
        context.setVariable("userName", user.getUsername());
        context.setVariable("password", temporaryPassword);

        this.sendMail(TEMPLATE_ENUM.TEMPLATE_RECOVER_PASSWORD.getValue(),context,user.getEmail(),"Recuperar contraseña 🚀",null,null);
    }

    /**
     * Emails the user with a balance report for a specified date range.
     *
     * @param response  A list containing the PDF report as a byte array, the filename, and the patient's name.
     * @param patientId The ID of the patient to whom the email will be sent.
     * @param startDate The start date of the balance report.
     * @param endDate   The end date of the balance report.
     * @throws MessagingException if there is an error while sending the email.
     */
    public void sendBalancesMailToUserMail(List<Object> response, Long patientId,
                                           Instant startDate, Instant endDate) throws MessagingException {

        log.info("Sending balance mail for patientId: {}", patientId);

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

        this.sendMail(TEMPLATE_ENUM.TEMPLATE_BALANCE_REPORT.getValue(),context,patient.getUser().getEmail(),
                String.format(
                "Balance de %s de %s a %s 🚀",
                patient.getName(),
                startDate.atZone(zone).format(formatterDay),
                endDate.atZone(zone).format(formatterDay)),(byte[]) response.get(0),(String) response.get(2));
    }

    /**
     * Helper method to send an email with the specified template, context, recipient email, subject, and optional PDF attachment.
     *
     * @param templateName The name of the email template to use for generating the email content.
     * @param context      The Thymeleaf context containing variables to be replaced in the email template.
     * @param email        The recipient's email address.
     * @param subject      The subject of the email.
     * @param pdfBytes     Optional byte array of a PDF file to be attached to the email. Can be null if no attachment is needed.
     * @param fileName     The name of the PDF file to be attached. Required if pdfBytes is not null.
     * @throws MessagingException if there is an error while sending the email.
     */
    private void sendMail(String templateName, Context context, String email,String subject,byte[] pdfBytes,String fileName) throws MessagingException {
        MailTemplate template = mailTemplateRepository
                .findByName(templateName);

        String htmlContent = templateEngine.process(template.getContent(), context);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(fromEmail);
        helper.setTo(email);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);
        if(Objects.nonNull(pdfBytes)) {
            helper.addAttachment(
                    fileName,
                    new ByteArrayResource(pdfBytes));
        }
        mailSender.send(message);
        log.info("Successful send mail {}", email);
    }
}

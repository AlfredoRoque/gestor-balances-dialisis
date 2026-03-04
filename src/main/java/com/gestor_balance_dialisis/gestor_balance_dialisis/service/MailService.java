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

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

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

    @Value("${sendgrid.api.key}")
    private String apiKey;

    @Value("${app.mail.from}")
    private String fromEmail;

    /**
     * Sends a recovery email to the specified user with a temporary password.
     *
     * @param user              the user to whom the recovery email will be sent
     * @param temporaryPassword the temporary password to include in the email
     */
    public void sendMailToRecoverPassword(User user, String temporaryPassword) {

        log.info("Sending recovery mail for user: {}", user.getUsername());

        Context context = new Context();
        context.setVariable("userName", user.getUsername());
        context.setVariable("password", temporaryPassword);

        sendMail(
                TEMPLATE_ENUM.TEMPLATE_RECOVER_PASSWORD.getValue(),
                context,
                user.getEmail(),
                Constants.RECOVER_PASSWORD_SUBJECT,
                null,
                null
        );
    }

    /**
     * Sends a balance report email to the specified patient with the generated PDF report attached.
     *
     * @param response   the list containing the PDF bytes and file name for the balance report
     * @param patientId  the ID of the patient to whom the email will be sent
     * @param startDate  the start date of the balance report period
     * @param endDate    the end date of the balance report period
     */
    public void sendBalancesMailToUserMail(List<Object> response,
                                           Long patientId,
                                           Instant startDate,
                                           Instant endDate) {

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

        String subject = String.format(
                Constants.BALANCE_FILE_NAME,
                patient.getName(),
                startDate.atZone(zone).format(formatterDay),
                endDate.atZone(zone).format(formatterDay)
        );

        sendMail(TEMPLATE_ENUM.TEMPLATE_BALANCE_REPORT.getValue(),
                context, SecurityUtils.getUserEmail(), subject, (byte[]) response.get(0), (String) response.get(2)
        );
    }

    /**
     * Prepares and sends an email using the SendGrid API with the specified template, context, recipient email, subject, and optional PDF attachment.
     *
     * @param templateName the name of the email template to use
     * @param context      the Thymeleaf context containing variables for template processing
     * @param email        the recipient's email address
     * @param subject      the subject of the email
     * @param pdfBytes     the byte array of the PDF attachment (optional)
     * @param fileName     the name of the PDF file to attach (optional)
     */
    private void sendMail(String templateName, Context context, String email,
                          String subject, byte[] pdfBytes, String fileName) {
        log.info("Preparing mail for {}", email);

        MailTemplate template = mailTemplateRepository.findByName(templateName);
        String htmlContent = templateEngine.process(template.getContent(), context);

        Email from = new Email(fromEmail);
        Email to = new Email(email);

        Mail mail = new Mail();
        mail.setFrom(from);
        mail.setSubject(subject);

        Personalization personalization = new Personalization();
        personalization.addTo(to);
        mail.addPersonalization(personalization);

        mail.addContent(new Content("text/plain", Constants.NEW_MAIL_MESSAGE_NOTE));
        mail.addContent(new Content("text/html", htmlContent));

        if (Objects.nonNull(pdfBytes)) {
            Attachments attachments = new Attachments();
            attachments.setFilename(fileName);
            attachments.setType("application/pdf");
            attachments.setDisposition("attachment");
            attachments.setContent(
                    Base64.getEncoder().encodeToString(pdfBytes));
            mail.addAttachments(attachments);
        }

        SendGrid sg = new SendGrid(apiKey);
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint(Constants.SEND_MAIL_PROVIDER_ENDPOINT);
            request.setBody(mail.build());

            log.info("Sending mail via SendGrid API...");
            Response response = sg.api(request);

            log.info("SendGrid Status Code: {}", response.getStatusCode());
            if (response.getStatusCode() != 202) {
                log.error("SendGrid error body: {}", response.getBody());
                throw new BalanceGlobalException(Constants.SEND_MAIL_ERROR, HttpStatus.CONFLICT.value());
            }

            log.info("Successful send mail to {}", email);
        } catch (IOException ex) {
            log.error("MAIL ERROR", ex);
            throw new BalanceGlobalException(Constants.SEND_MAIL_ERROR, HttpStatus.CONFLICT.value());
        }
    }
}
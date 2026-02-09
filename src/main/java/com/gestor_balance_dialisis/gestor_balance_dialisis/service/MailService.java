package com.gestor_balance_dialisis.gestor_balance_dialisis.service;

import com.gestor_balance_dialisis.gestor_balance_dialisis.entity.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

/**
 * Service for sending emails to users, specifically for password recovery purposes.
 * It uses JavaMailSender to send emails and Thymeleaf TemplateEngine to generate email content from templates.
 */
@RequiredArgsConstructor
@Service
public class MailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    /**
     * Email the user with a temporary password for password recovery.
     *
     * @param user              The user to whom the email will be sent, containing their email and username.
     * @param temporaryPassword The temporary password to be included in the email content.
     * @throws MessagingException if there is an error while sending the email.
     */
    public void sendMailToRecoverPassword(User user, String temporaryPassword) throws MessagingException {

        Context context = new Context();
        context.setVariable("userName", user.getUsername());
        context.setVariable("password", temporaryPassword);

        String htmlContent = templateEngine.process("recover-password-email", context);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(user.getEmail());
        helper.setSubject("Recuperar contraseña 🚀");
        helper.setText(htmlContent, true);

        mailSender.send(message);
    }
}

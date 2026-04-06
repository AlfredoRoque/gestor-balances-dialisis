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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Test class for {@link MailService}.
 * <p>
 * This class contains unit tests for the MailService, focusing on the methods responsible for sending emails
 * to recover passwords and to send balance reports. The tests verify that the service correctly processes
 * email templates, handles missing patients, and interacts with the template engine and repositories as expected.
 * <p>
 * Note: The actual sending of emails through SendGrid is not tested here due to the lack of a configured API key in the test environment. Instead, we verify that the service attempts to send the email and handles the resulting exception appropriately.
 */
@ExtendWith(MockitoExtension.class)
class MailServiceTest {

    @Mock private TemplateEngine templateEngine;
    @Mock private PatientRepository patientRepository;
    @Mock private MailTemplateRepository mailTemplateRepository;

    @InjectMocks private MailService mailService;

    // ─── sendMailToRecoverPassword ─────────────────────────────────────────────

    /**
     * Test that sendMailToRecoverPassword processes the template with the correct variables.
     * Verifies that the template engine is called with the recovery password template content
     * and that the mail template repository is queried for the correct template name.
     */
    @Test
    void sendMailToRecoverPassword_processesTemplate() {
        User user = new User();
        user.setUsername("admin");
        user.setEmail("admin@mail.com");

        MailTemplate template = new MailTemplate(1L, TEMPLATE_ENUM.TEMPLATE_RECOVER_PASSWORD.getValue(), "<html>{{password}}</html>");
        when(mailTemplateRepository.findByName(TEMPLATE_ENUM.TEMPLATE_RECOVER_PASSWORD.getValue()))
                .thenReturn(template);
        when(templateEngine.process(eq(template.getContent()), any(Context.class)))
                .thenReturn("<html>tempPwd123</html>");

        // The actual SendGrid call will throw because apiKey is null in test,
        // so we expect a BalanceGlobalException wrapping the send error
        assertThatThrownBy(() -> mailService.sendMailToRecoverPassword(user, "tempPwd123"))
                .isInstanceOf(BalanceGlobalException.class)
                .hasMessageContaining(Constants.SEND_MAIL_ERROR);

        verify(mailTemplateRepository).findByName(TEMPLATE_ENUM.TEMPLATE_RECOVER_PASSWORD.getValue());
        verify(templateEngine).process(eq(template.getContent()), any(Context.class));
    }

    // ─── sendBalancesMailToUserMail ────────────────────────────────────────────

    /**
     * Test that sendBalancesMailToUserMail throws an exception when the patient is not found.
     * Verifies that a {@link BalanceGlobalException} with PATIENT_NOT_FOUND message is thrown
     * when no patient matches the provided ID.
     */
    @Test
    void sendBalancesMailToUserMail_patientNotFound_throwsException() {
        when(patientRepository.findById(99L)).thenReturn(Optional.empty());

        List<Object> response = List.of(new byte[]{1, 2, 3}, "application/pdf", "report.pdf");
        Instant start = Instant.parse("2024-01-01T00:00:00Z");
        Instant end = Instant.parse("2024-01-31T00:00:00Z");

        assertThatThrownBy(() -> mailService.sendBalancesMailToUserMail(response, 99L, start, end))
                .isInstanceOf(BalanceGlobalException.class)
                .hasMessageContaining(Constants.PATIENT_NOT_FOUND);
    }

    /**
     * Test that sendBalancesMailToUserMail retrieves the patient and processes the template.
     * Verifies that when a valid patient ID is provided, the service fetches the patient,
     * builds the email context, and attempts to send the mail through the template engine.
     */
    @Test
    void sendBalancesMailToUserMail_validPatient_processesTemplate() {
        User owner = new User();
        owner.setUsername("doctor");

        Patient patient = new Patient();
        patient.setId(10L);
        patient.setName("Juan");
        patient.setUser(owner);

        when(patientRepository.findById(10L)).thenReturn(Optional.of(patient));

        MailTemplate template = new MailTemplate(2L, TEMPLATE_ENUM.TEMPLATE_BALANCE_REPORT.getValue(), "<html>report</html>");
        when(mailTemplateRepository.findByName(TEMPLATE_ENUM.TEMPLATE_BALANCE_REPORT.getValue()))
                .thenReturn(template);
        when(templateEngine.process(eq(template.getContent()), any(Context.class)))
                .thenReturn("<html>rendered</html>");

        List<Object> response = List.of(new byte[]{1, 2, 3}, "application/pdf", "report.pdf");
        Instant start = Instant.parse("2024-01-01T00:00:00Z");
        Instant end = Instant.parse("2024-01-31T00:00:00Z");

        try (MockedStatic<SecurityUtils> su = mockStatic(SecurityUtils.class)) {
            su.when(SecurityUtils::getUserZone).thenReturn(ZoneId.of("UTC"));
            su.when(SecurityUtils::getUserEmail).thenReturn("doctor@mail.com");

            // SendGrid call will fail because apiKey is null in test context
            assertThatThrownBy(() -> mailService.sendBalancesMailToUserMail(response, 10L, start, end))
                    .isInstanceOf(BalanceGlobalException.class)
                    .hasMessageContaining(Constants.SEND_MAIL_ERROR);
        }

        verify(patientRepository).findById(10L);
        verify(mailTemplateRepository).findByName(TEMPLATE_ENUM.TEMPLATE_BALANCE_REPORT.getValue());
        verify(templateEngine).process(eq(template.getContent()), any(Context.class));
    }
}


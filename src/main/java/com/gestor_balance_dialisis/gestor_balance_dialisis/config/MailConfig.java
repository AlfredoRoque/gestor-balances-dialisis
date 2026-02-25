package com.gestor_balance_dialisis.gestor_balance_dialisis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.StringTemplateResolver;

/**
 * Configuration class for setting up Thymeleaf template resolvers for email templates.
 * This configuration defines a StringTemplateResolver that allows processing of HTML templates defined as strings.
 * The resolver is set to be non-cacheable and has a higher priority than other resolvers to ensure it is used for email templates.
 */
@Configuration
public class MailConfig {

    /**
     * Configures a StringTemplateResolver for Thymeleaf to process HTML templates defined as strings.
     * This resolver is set to be non-cacheable and has a higher priority than other resolvers.
     *
     * @return a configured StringTemplateResolver bean
     */
    @Bean
    public StringTemplateResolver stringTemplateResolver() {
        StringTemplateResolver resolver = new StringTemplateResolver();
        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setCacheable(false);
        resolver.setOrder(1); // priority for string templates(higher)
        return resolver;
    }
}

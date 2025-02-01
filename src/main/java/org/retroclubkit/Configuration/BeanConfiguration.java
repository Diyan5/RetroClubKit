package org.retroclubkit.Configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Properties;

@Configuration
public class BeanConfiguration {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JavaMailSender mailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com"); // Или "smtp.abv.bg" за ABV
        mailSender.setPort(587); // Gmail: 587 (STARTTLS) или 465 (SSL), ABV: 465 (SSL)
        mailSender.setUsername("paskalevdiyan@gmail.com"); // Твоят Gmail/ABV имейл
        mailSender.setPassword("cubvjzkyiotltzpn"); // Генерираната парола за приложение

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true"); // За Gmail (false за ABV)
        props.put("mail.smtp.ssl.enable", "false"); // За Gmail (true за ABV)
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com"); // Променяй на smtp.abv.bg за ABV
        props.put("mail.debug", "true"); // Включва debug логове

        return mailSender;
    }

}

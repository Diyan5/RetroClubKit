package org.retroclubkit.contact.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.retroclubkit.contact.repository.ContactRepository;
import org.retroclubkit.web.dto.ContactRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;
import org.retroclubkit.contact.model.ContactMessage;

@Service
public class ContactService {

    private final ContactRepository contactRepository;
    private final JavaMailSender mailSender; // 🔹 За изпращане на имейли

    @Autowired
    public ContactService(ContactRepository contactRepository, JavaMailSender mailSender) {
        this.contactRepository = contactRepository;
        this.mailSender = mailSender;
    }

    @Transactional
    public void saveMessage(ContactRequest contactRequest) {
        ContactMessage message = ContactMessage.builder()
                .name(contactRequest.getName())
                .email(contactRequest.getEmail())
                .subject(contactRequest.getSubject())
                .message(contactRequest.getMessage())
                .build();
        contactRepository.save(message);
    }

    public void sendEmail(ContactRequest contactRequest) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo("paskalevdiyan@gmail.com");
       // mailMessage.setCc(contactRequest.getEmail()); if i want to sent a duplicate
        mailMessage.setSubject("New Contact Form Submission: " + contactRequest.getSubject());
        mailMessage.setText(
                        "From: " + contactRequest.getName() + "\n" +
                        "Email: " + contactRequest.getEmail() + "\n\n" +
                        "Subject: " + contactRequest.getSubject()  + "\n" +
                        "Message: " + contactRequest.getMessage()
        );

        mailSender.send(mailMessage);
    }

}

package com.faspix.notificationservice.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class EmailService {

    private JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String fromAddress;

    public void sendEmail(String email, String subject, String body) {
        if (email == null) {
            throw new IllegalArgumentException("Email cannot be null");
        }
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(email);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }

}

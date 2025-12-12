package com.numa.generic;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Service
public class EmailService {

    @Autowired
    JavaMailSender javaMailSender;


    //logger
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Async
    public void sendOtp(String toEmail, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(text);
        javaMailSender.send(message);
    }

    public void sendEmail(String to, String subject, String body) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body);

            javaMailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email.", e);
        }
    }


    public void sendAccountLockoutEmail(String toEmail, Instant lockUntil) {

        String formattedLockUntil = lockUntil.atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z"));

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Account Locked Due to Failed Login Attempts");
        message.setText(
                "Hello,\n\n" +
                        "Your account has been locked due to 5 failed login attempts. " +
                        "It will remain locked until " + formattedLockUntil + ". Please try again after this time.\n\n" +
                        "If this wasn't you, please contact support immediately.\n\n" +
                        "Thank you,\nYour Team"
        );
        javaMailSender.send(message);
    }
}
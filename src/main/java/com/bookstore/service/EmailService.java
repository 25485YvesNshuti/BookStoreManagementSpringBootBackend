package com.bookstore.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    /**
     * Send a general email.
     */
    public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

    /**
     * Send an OTP email specifically for Two-Factor Authentication.
     */
    public void sendOtpEmail(String to, String otp) {
        String subject = "Your OTP for Login";
        String text = "Your OTP is: " + otp + ". It is valid for 5 minutes.";
        sendEmail(to, subject, text);
    }
}
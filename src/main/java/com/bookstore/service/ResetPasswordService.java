package com.bookstore.service;

import com.bookstore.entity.ResetToken;
import com.bookstore.entity.User;
import com.bookstore.repository.ResetTokenRepository;
import com.bookstore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class ResetPasswordService {

    @Autowired
    private ResetTokenRepository resetTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    /**
     * Request a password reset by generating a token and sending an email.
     */
    public void requestPasswordReset(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("Error: Email not found!");
        }

        User user = userOptional.get();
        String token = UUID.randomUUID().toString();

        // Remove any existing tokens for the user
        resetTokenRepository.deleteByUserId(user.getId());

        // Create and save a new reset token
        ResetToken resetToken = new ResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpirationDate(LocalDateTime.now().plusHours(1)); // Token valid for 1 hour
        resetTokenRepository.save(resetToken);

        // Send the reset link via email
        String resetLink = "http://localhost:8080/api/auth/confirm-reset?token=" + token;
        emailService.sendEmail(
                user.getEmail(),
                "Password Reset Request",
                "Click the link to reset your password: " + resetLink
        );
    }

    /**
     * Confirm the password reset by validating the token and updating the password.
     */
    public void confirmPasswordReset(String token, String newPassword) {
        Optional<ResetToken> resetTokenOptional = resetTokenRepository.findByToken(token);
        if (resetTokenOptional.isEmpty() ||
                resetTokenOptional.get().getExpirationDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Error: Invalid or expired token!");
        }

        ResetToken resetToken = resetTokenOptional.get();
        User user = resetToken.getUser();

        // Update the user's password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Delete the used reset token
        resetTokenRepository.delete(resetToken);
    }
}
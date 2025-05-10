package com.bookstore.service;

import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.Random;

@Service
public class OtpService {

    // Use a thread-safe ConcurrentHashMap to store OTPs
    private final ConcurrentMap<String, OtpDetails> otpStorage = new ConcurrentHashMap<>();

    private static final int OTP_EXPIRATION_MINUTES = 5; // OTP validity duration

    /**
     * Generate and store a 6-digit OTP for a given username.
     */
    public String generateOtp(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        
        Random random = new Random();
        String otp = String.format("%06d", random.nextInt(1000000));

        // Save OTP with expiry time
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(OTP_EXPIRATION_MINUTES);
        otpStorage.put(username.toLowerCase(), new OtpDetails(otp, expiryTime));

        System.out.println("Generated OTP for " + username + ": " + otp + " (expires at " + expiryTime + ")");
        return otp;
    }

    /**
     * Validate the OTP for a given username.
     */
    public boolean validateOtp(String username, String otp) {
        if (username == null || username.trim().isEmpty() || otp == null || otp.trim().isEmpty()) {
            System.out.println("Username or OTP is null or empty");
            return false;
        }
        
        System.out.println("Validating OTP for username: " + username + " with OTP: " + otp);
        
        // Convert username to lowercase to ensure case insensitivity
        String normalizedUsername = username.toLowerCase();
        
        // Debug: print all keys in the otpStorage
        System.out.println("Current OTP storage keys: " + String.join(", ", otpStorage.keySet()));
        
        if (!otpStorage.containsKey(normalizedUsername)) {
            System.out.println("No OTP found for username: " + username);
            return false; // No OTP exists for the username
        }

        OtpDetails otpDetails = otpStorage.get(normalizedUsername);

        // Check if the OTP has expired
        if (LocalDateTime.now().isAfter(otpDetails.getExpiryTime())) {
            System.out.println("OTP expired for username: " + username);
            otpStorage.remove(normalizedUsername); // Remove expired OTP
            return false;
        }

        // Validate OTP
        boolean isValid = otpDetails.getOtp().equals(otp);
        if (isValid) {
            otpStorage.remove(normalizedUsername); // OTP can only be used once
            System.out.println("OTP validated successfully and removed for username: " + username);
        } else {
            System.out.println("Invalid OTP for username: " + username + ". Expected: " + otpDetails.getOtp());
        }

        return isValid;
    }

    /**
     * Manually clear an OTP for a specific username (useful for testing and debugging)
     */
    public void clearOtp(String username) {
        if (username != null) {
            otpStorage.remove(username.toLowerCase());
            System.out.println("Manually cleared OTP for username: " + username);
        }
    }

    /**
     * Check if an OTP exists for a username (for debugging)
     */
    public boolean hasOtp(String username) {
        if (username == null) return false;
        return otpStorage.containsKey(username.toLowerCase());
    }

    /**
     * Inner class to store OTP details (value and expiry time).
     */
    private static class OtpDetails {
        private final String otp;
        private final LocalDateTime expiryTime;

        public OtpDetails(String otp, LocalDateTime expiryTime) {
            this.otp = otp;
            this.expiryTime = expiryTime;
        }

        public String getOtp() {
            return otp;
        }

        public LocalDateTime getExpiryTime() {
            return expiryTime;
        }
    }
}
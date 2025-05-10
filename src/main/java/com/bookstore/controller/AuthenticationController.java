package com.bookstore.controller;

import com.bookstore.entity.Role;
import com.bookstore.entity.User;
import com.bookstore.payload.JwtResponse;
import com.bookstore.payload.LoginRequest;
import com.bookstore.payload.SignupRequest;
import com.bookstore.repository.RoleRepository;
import com.bookstore.repository.UserRepository;
import com.bookstore.security.JwtUtils;
import com.bookstore.service.EmailService;
import com.bookstore.service.OtpService;
import com.bookstore.service.ResetPasswordService;
import com.bookstore.service.UserDetailsImpl;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "http://localhost:3000")

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private ResetPasswordService resetPasswordService;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private OtpService otpService;

    @Autowired
    private EmailService emailService;

    /**
     * Handle user login and OTP generation.
     */
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            if (!userDetails.isEnabled()) {
                return ResponseEntity.badRequest().body("Account is not verified. Please check your email.");
            }

            SecurityContextHolder.getContext().setAuthentication(authentication);

            otpService.clearOtp(userDetails.getUsername());

            String otp = otpService.generateOtp(userDetails.getUsername());

            System.out.println("Generated OTP for user " + userDetails.getUsername() + ": " + otp);

            emailService.sendOtpEmail(userDetails.getEmail(), otp);

            return ResponseEntity.ok("OTP sent to your email. Please verify to complete login.");

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid username or password.");
        }
    }

    /**
     * Validate the OTP and complete login by issuing a JWT.
     */
    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestParam String username, @RequestParam String otp) {
        System.out.println("Verifying OTP: username=" + username + ", otp=" + otp);

        if (username == null || username.trim().isEmpty() || otp == null || otp.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Username and OTP are required.");
        }

        System.out.println("OTP exists for " + username + ": " + otpService.hasOtp(username));

        if (!otpService.validateOtp(username, otp)) {
            return ResponseEntity.badRequest().body("Invalid or expired OTP.");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Error: User not found."));

        UserDetailsImpl userDetails = UserDetailsImpl.build(user);
        String jwt = jwtUtils.generateJwtToken(userDetails.getUsername());

        return ResponseEntity.ok(
                new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(), userDetails.getAuthorities()));
    }

    /**
     * Handle user registration and role assignment.
     */
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignupRequest signupRequest) {
        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            return ResponseEntity.badRequest().body("Error: Username is already taken!");
        }

        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            return ResponseEntity.badRequest().body("Error: Email is already in use!");
        }

        User user = new User();
        user.setUsername(signupRequest.getUsername());
        user.setEmail(signupRequest.getEmail());
        user.setPassword(encoder.encode(signupRequest.getPassword()));
        user.setFirstName(signupRequest.getFirstName());
        user.setLastName(signupRequest.getLastName());
        user.setPhoneNumber(signupRequest.getPhoneNumber());
        user.setEnabled(false); // Initially set to false, needs verification

        String verificationCode = generateVerificationCode();
        user.setVerificationCode(verificationCode);

        Set<Role> roles = new HashSet<>();
        if (signupRequest.getRoles() == null || signupRequest.getRoles().isEmpty()) {
            Role userRole = roleRepository.findByName(Role.ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            signupRequest.getRoles().forEach(role -> {
                Role userRole = roleRepository.findByName(Role.ERole.valueOf(role))
                        .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                roles.add(userRole);
            });
        }

        user.setRoles(roles);
        userRepository.save(user);

        String verificationLink = "http://localhost:3001/verify-account?username=" + user.getUsername() + "&code=" + user.getVerificationCode();
        emailService.sendEmail(user.getEmail(), "Account Verification",
                "Your verification code is: " + verificationCode + ". Please use the link below to verify your account: " + verificationLink);

        return ResponseEntity.ok("User registered successfully! Please check your email for the verification code to activate your account.");
    }

    /**
     * Verify the account using the verification code sent after signup.
     */
    @GetMapping("/verify-account")
    public ResponseEntity<?> verifyAccount(@RequestParam String username, @RequestParam String code) {
        System.out.println("Verifying account for user: " + username + " with code: " + code);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Error: User not found."));

        if (user.getVerificationCode() == null || !user.getVerificationCode().equals(code)) {
            return ResponseEntity.badRequest().body("Invalid or expired verification code.");
        }

        user.setEnabled(true);
        user.setVerificationCode(null); // Clear the verification code after successful verification
        userRepository.save(user);

        return ResponseEntity.ok("Account verified successfully! You can now log in.");

        // Optional: Generate JWT and log in the user automatically
        // UserDetailsImpl userDetails = UserDetailsImpl.build(user);
        // String jwt = jwtUtils.generateJwtToken(userDetails.getUsername());
        // return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(), userDetails.getAuthorities()));
    }

    /**
     * Helper method to generate a verification code.
     */
    private String generateVerificationCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000)); // 6-digit code
    }

    /**
     * Request a password reset by generating a reset token and sending an email.
     */
    @PostMapping("/reset-password")
    public ResponseEntity<?> requestPasswordReset(@RequestParam String email) {
        try {
            resetPasswordService.requestPasswordReset(email);
            return ResponseEntity.ok("Password reset email sent!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Confirm the password reset by validating the token and setting a new password.
     */
    @PostMapping("/confirm-reset")
    public ResponseEntity<?> confirmPasswordReset(
            @RequestParam String token, @RequestParam String newPassword) {
        try {
            resetPasswordService.confirmPasswordReset(token, newPassword);
            return ResponseEntity.ok("Password reset successfully!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
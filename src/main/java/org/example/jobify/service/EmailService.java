package org.example.jobify.service;

import lombok.RequiredArgsConstructor;
import org.example.jobify.model.User;
import org.example.jobify.repository.MyUserDetailsRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final MyUserDetailsRepository myUserDetailsRepository;
    private final JavaMailSender emailSender;
    @Value("${mail.email}")
    private String setFromEmail;

    void sendVerificationEmail(String username, String verificationToken) {
        String subject = "Email Verification";
        String message = "Dear " + username + ",\n\n This is the otp to access Jobify \n" + verificationToken;

        // Create the email message
        System.out.println(message);
        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(username); // Assuming the username is the email
        email.setSubject(subject);
        email.setText(message);
        email.setFrom(setFromEmail);// Replace with your email

        // Send the email
        try {
            emailSender.send(email);
        }catch (Exception e) {
            System.out.println(e);
        }
    }

    public String generateVerificationToken(User user) {
        // Generate a 4-digit random OTP
        SecureRandom random = new SecureRandom();
        int otp = 1000 + random.nextInt(9000); // Generates a number between 1000 and 9999

        String otpToken = String.valueOf(otp); // Convert to String
        user.setVerificationToken(otpToken);  // Save OTP to the user object
        myUserDetailsRepository.save(user);   // Store user with OTP in DB
        return otpToken;
    }
}

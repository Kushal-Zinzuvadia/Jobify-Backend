package org.example.jobify.service;

import lombok.RequiredArgsConstructor;
import org.example.jobify.model.Role;
import org.example.jobify.model.RoleName;
import org.example.jobify.model.User;
import org.example.jobify.model.UserPrincipal;
import org.example.jobify.repository.MyUserDetailsRepository;
import org.example.jobify.repository.RoleRepository;
import org.example.jobify.repository.UserRepository;
import org.example.jobify.utilities.ApiException;
import org.hibernate.validator.internal.util.stereotypes.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private UserRepository userRepository;

    private final MyUserDetailsRepository myUserDetailsRepository;

    private final AuthenticationManager authenticationManager;

    @Lazy
    private JwtService jwtService;

    private final RoleRepository roleRepository;

    private final EmailService emailService;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public User registerUser(User user) throws ApiException, MailAuthenticationException {
        User registeredUser;
        Optional<User> existingUser = myUserDetailsRepository.findByEmail(user.getEmail());
        if (existingUser.isPresent()) {
            if(existingUser.get().isVerified()){
                throw new ApiException("User already exists and is verified", HttpStatus.BAD_REQUEST.value());
            }
            user = existingUser.get();
        }
        user.setPassword(this.encoder.encode(user.getPassword()));
        user.setVerified(false);  // Mark user as not verified initially
        Optional<Role> userRole = roleRepository.findByName(RoleName.JOB_SEEKER);

        user.setRoleName(userRole.get().getName());

        // Save the user in database first and then send token to get then verified
        String verificationToken = emailService.generateVerificationToken(user);

        emailService.sendVerificationEmail(user.getEmail(), verificationToken);
        System.out.println(user);

        registeredUser = this.myUserDetailsRepository.save(user);
        return registeredUser;
    }
    // Send verification email

    public Map<String,Object> verifyUserEmail(String token) throws ApiException {
        Optional<User> requestUser = myUserDetailsRepository.findByVerificationToken(token);
        if (requestUser.isEmpty()) {
            throw new ApiException("Invalid verification token",HttpStatus.UNAUTHORIZED.value());
        }
        if (!requestUser.get().isVerified()) {
            User user = requestUser.get();
            user.setVerified(true);  // Mark the user as verified
            user.setVerificationToken(null);  // Clear the token after verification
            myUserDetailsRepository.save(user);

            // Generate a JWT token after email verification
            Map<String,Object> userMap = new HashMap<>();
            userMap.put("jwtToken",jwtService.generateToken(user.getEmail()));
            userMap.put("user", user);
            return userMap;
        } else {
            throw new ApiException("Invalid or already verified email",400);
        }
    }




    public String login(User user) throws ApiException , BadCredentialsException {
        System.out.println("login service");
        // Attempt to authenticate the user using the authentication manager
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword())
        );
        System.out.println("Auth:"+auth);
        // If authentication is successful, set the authentication in the SecurityContext
        if (auth.isAuthenticated()) {
            // Explicitly set the Authentication in the SecurityContext
            SecurityContextHolder.getContext().setAuthentication(auth);

            // Generate and return the JWT token after successful authentication
            return this.jwtService.generateToken(user.getEmail());
        } else {
            System.out.println("Authentication Failed");
            throw new ApiException("Authentication failed", 401);
        }

    }
}

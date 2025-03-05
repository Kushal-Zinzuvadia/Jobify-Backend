package org.example.jobify.service;

import lombok.RequiredArgsConstructor;
import org.example.jobify.model.*;
import org.example.jobify.repository.JobRepository;
import org.example.jobify.repository.MyUserDetailsRepository;
import org.example.jobify.repository.RoleRepository;
import org.example.jobify.repository.UserRepository;
import org.example.jobify.utilities.ApiException;
import org.hibernate.validator.internal.util.stereotypes.Lazy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JobRepository jobRepository;

    private AuthenticationManager authenticationManager;

    private RoleRepository roleRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public UserService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public User registerUser(@RequestBody User user) throws ApiException, MailAuthenticationException {
        User registeredUser;
        System.out.println(user.getEmail());
        System.out.println(user.getRoleName());

//        Optional<User> existingUser = myUserDetailsRepository.findByEmail(user.getEmail());
//        if (existingUser.isPresent()) {
//            throw new ApiException("User already exists and is verified", HttpStatus.BAD_REQUEST.value());
////            if(existingUser.get().isVerified()){
////
////            }
//        }
        user.setPassword(this.encoder.encode(user.getPassword()));
//        user.setVerified(false);  // Mark user as not verified initially
        Optional<Role> userRole = roleRepository.findByName(RoleName.JOB_SEEKER);

        user.setRoleName(user.getRoleName());

        // Save the user in database first and then send token to get then verified
//        String verificationToken = emailService.generateVerificationToken(user);

//        emailService.sendVerificationEmail(user.getEmail(), verificationToken);
        System.out.println(user);

        registeredUser = this.userRepository.save(user);
        return registeredUser;
    }

    // Send verification email

//    public Map<String,Object> verifyUserEmail(String token) throws ApiException {
//        Optional<User> requestUser = myUserDetailsRepository.findByVerificationToken(token);
//        if (requestUser.isEmpty()) {
//            throw new ApiException("Invalid verification token",HttpStatus.UNAUTHORIZED.value());
//        }
//        if (!requestUser.get().isVerified()) {
//            User user = requestUser.get();
//            user.setVerified(true);  // Mark the user as verified
//            user.setVerificationToken(null);  // Clear the token after verification
//            myUserDetailsRepository.save(user);
//
//            // Generate a JWT token after email verification
//            Map<String,Object> userMap = new HashMap<>();
//            userMap.put("jwtToken",jwtService.generateToken(user.getEmail()));
//            userMap.put("user", user);
//            return userMap;
//        } else {
//            throw new ApiException("Invalid or already verified email",400);
//        }
//    }

    public String login(User user) throws ApiException , BadCredentialsException {
        System.out.println("login service");
        // Attempt to authenticate the user using the authentication manager
//        Authentication auth = authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword())
//        );
//        System.out.println("Auth:"+auth);
//        // If authentication is successful, set the authentication in the SecurityContext
//        if (auth.isAuthenticated()) {
//            // Explicitly set the Authentication in the SecurityContext
//            SecurityContextHolder.getContext().setAuthentication(auth);
//
//            // Generate and return the JWT token after successful authentication
//            return this.jwtService.generateToken(user.getEmail());
//        } else {
//            System.out.println("Authentication Failed");
//            throw new ApiException("Authentication failed", 401);
//        }
        return "Sample token";
    }

    // 1. Apply for a job
    public void applyToJob(UUID userId, UUID jobId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        user.getJobs().add(job);
        job.getUsers().add(user);

        userRepository.save(user);
        jobRepository.save(job);
    }

    // 2. Withdraw application
    public void withdrawApplication(UUID userId, UUID jobId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        user.getJobs().remove(job);
        job.getUsers().remove(user);

        userRepository.save(user);
        jobRepository.save(job);
    }

    // 3. Get jobs applied by a user
    public List<Job> getJobsByUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getJobs();
    }

    // 11. Get all users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // 12. Get user by ID
    public User getUserById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // 13. Create a user
    public User createUser(User user) {
        return userRepository.save(user);
    }

    // 14. Delete a user
    public void deleteUser(UUID userId) {
        userRepository.deleteById(userId);
    }
}

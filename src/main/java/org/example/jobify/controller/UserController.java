package org.example.jobify.controller;

import jakarta.validation.Valid;
import org.example.jobify.dtos.OtpDto;
import org.example.jobify.model.Application;
import org.example.jobify.model.Job;
import org.example.jobify.model.RoleName;
import org.example.jobify.model.User;
import org.example.jobify.repository.ApplicationRepository;
import org.example.jobify.repository.JobRepository;
import org.example.jobify.repository.UserRepository;
import org.example.jobify.request.UserRoleRequest;
import org.example.jobify.service.UserService;
import org.example.jobify.utilities.ApiException;
import org.example.jobify.utilities.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:5173"})
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    ApplicationContext context;

    @Autowired
    @Lazy
    private UserService userService;

    @PostMapping(path = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody User user) {
        try {
            User registeredUser = this.userService.registerUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse(201,registeredUser,"User created successfully"));
        }catch (ApiException e){
            System.out.println(e.getMessage());
            return ResponseEntity.status(e.getStatusCode()).body(new ApiResponse(e.getStatusCode(),null,e.getMessage()));
        }
    }

//    @PostMapping("/verify")
//    public ResponseEntity<ApiResponse> verify(@RequestBody OtpDto otpDto){
//        try {
//            // Verify the user and generate a JWT token
//            System.out.println(otpDto.getOtp());
////            Map<String,Object> verifyResponse= userService.verifyUserEmail(otpDto.getOtp());
//            System.out.println(verifyResponse);
//            User currentUser = (User) (verifyResponse.get("user"));
//            String jwtToken = (String) verifyResponse.get("jwtToken");
//            // Prepare response with JWT token in the header and user data in the body
//            HttpHeaders headers = new HttpHeaders();
//            headers.set("Authorization", "Bearer " + jwtToken);
//            System.out.println(jwtToken);
//            ApiResponse response = new ApiResponse(200, currentUser, "Email verified successfully.");
//            return ResponseEntity.ok()
//                    .headers(headers)
//                    .body(response);
//        } catch (ApiException e) {
//            // Handle API exceptions (Invalid/expired token)
//            return ResponseEntity.status(e.getStatusCode()).body(new ApiResponse(e.getStatusCode(), null, e.getMessage()));
//        } catch (Exception e) {
//            // Catch any other unexpected exceptions
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), null, "An unexpected error occurred: " + e.getMessage()));
//        }
//    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody User user){
        try {
            Optional<User> existingUser = Optional.ofNullable(this.userService.findByEmail(user.getEmail()));
            if(existingUser.isEmpty()){
                throw new ApiException("Email doesnt exist...",400);
            }
            System.out.println("login Controller");
            System.out.println(user);
            String token = userService.login(user);
            System.out.println("token: " + token);
            User currentUser = existingUser.get();
            System.out.println("currentUser: " + currentUser);
            System.out.println("Controller Login");
            // Create response headers with the token
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);

            // Respond with token in headers and success message in the body
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(new ApiResponse(200, currentUser, "Login successful"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(HttpStatus.UNAUTHORIZED.value(), null, "Login failed: " + e.getMessage()));
        }catch (ApiException e){
            System.out.println(e.getMessage());
            return ResponseEntity.status(e.getStatusCode()).body(new ApiResponse(e.getStatusCode(), null, e.getMessage()));
        }catch (BadCredentialsException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(HttpStatus.UNAUTHORIZED.value(), null, "Invalid email or password: "));
        }
    }

    // 1. Fetch User Details by Email
    @GetMapping("/user/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        User user = userService.findByEmail(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(user);
    }

    // 2. Set User Role
//    @PostMapping("/set-role")
//    public ResponseEntity<?> setUserRole(@RequestBody UserRoleRequest request) {
//        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());
//
//        if (userOptional.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
//        }
//
//        User user = userOptional.get();
//        user.setRoleName(RoleName.valueOf(request.getRole()));
//        userRepository.save(user);
//        return ResponseEntity.ok("Role updated successfully");
//    }

    // 3. Get Applications for a Specific Job (For Employers)
    @GetMapping("/job/{jobId}/applicants")
    public ResponseEntity<?> getApplicantsForJob(@PathVariable UUID jobId) {
        List<Application> applications = applicationRepository.findByJobId(jobId);

        if (applications.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No applicants found for this job");
        }
        return ResponseEntity.ok(applications);
    }

    // 4. Get Jobs Applied by Job Seeker
    @GetMapping("/user/{email}/applications")
    public ResponseEntity<?> getApplicationsByUser(@PathVariable String email) {
        List<Application> applications = applicationRepository.findByUserEmail(email);

        if (applications.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No applications found for this user");
        }
        return ResponseEntity.ok(applications);
    }

    @PostMapping("/user/{userId}/apply/{jobId}")
    public ResponseEntity<?> applyJob(@PathVariable UUID userId, @PathVariable UUID jobId) {
        Optional<User> userOptional = userRepository.findById(userId);
        Optional<Job> jobOptional = jobRepository.findById(jobId);

        if (userOptional.isPresent() && jobOptional.isPresent()) {
            User user = userOptional.get();
            Job job = jobOptional.get();

            user.getJobs().add(job);

            job.getUsers().add(user);

            // Save both entities
            userRepository.save(user);
            jobRepository.save(job); // Ensure both sides persist

            return ResponseEntity.ok("Job application successful.");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User or Job not found.");
    }

    @DeleteMapping("/user/{userId}/withdraw/{jobId}")
    public ResponseEntity<?> withdrawApplication(@PathVariable UUID userId, @PathVariable UUID jobId) {
        userService.withdrawApplication(userId, jobId);
        return ResponseEntity.ok("Application withdrawn successfully");
    }

    @GetMapping("/user/{userId}/jobs")
    public List<Job> getJobsByUser(@PathVariable UUID userId) {
        return userService.getJobsByUser(userId);
    }

}

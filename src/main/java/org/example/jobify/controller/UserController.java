package org.example.jobify.controller;

import org.example.jobify.model.Application;
import org.example.jobify.model.Role;
import org.example.jobify.model.User;
import org.example.jobify.repository.ApplicationRepository;
import org.example.jobify.repository.UserRepository;
import org.example.jobify.request.UserRoleRequest;
import org.example.jobify.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:5173"})
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ApplicationRepository applicationRepository;


    @Autowired
    private UserService userService;

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
    @PostMapping("/set-role")
    public ResponseEntity<?> setUserRole(@RequestBody UserRoleRequest request) {
        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());

        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        User user = userOptional.get();
        user.setRole(Role.valueOf(request.getRole()));
        userRepository.save(user);
        return ResponseEntity.ok("Role updated successfully");
    }

    // 3. Get Applications for a Specific Job (For Employers)
    @GetMapping("/job/{jobId}/applicants")
    public ResponseEntity<?> getApplicantsForJob(@PathVariable Long jobId) {
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
}

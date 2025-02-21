package org.example.jobify.controller;

import org.example.jobify.model.Application;
import org.example.jobify.model.Job;
import org.example.jobify.model.User;
import org.example.jobify.repository.ApplicationRepository;
import org.example.jobify.repository.JobRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/applications")
public class ApplicationController {

    private final ApplicationRepository applicationRepository;
    private final JobRepository jobRepository;

    public ApplicationController(ApplicationRepository applicationRepository, JobRepository jobRepository) {
        this.applicationRepository = applicationRepository;
        this.jobRepository = jobRepository;
    }

    @GetMapping("/my-applications")
    public ResponseEntity<List<Application>> getMyApplications(@AuthenticationPrincipal User user) {
        List<Application> applications = applicationRepository.findByUser(user);
        return ResponseEntity.ok(applications);
    }

    @GetMapping("/job/{jobId}/applicants")
    public ResponseEntity<List<Application>> getApplicants(@PathVariable UUID jobId, @AuthenticationPrincipal User employer) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Job not found"));

        // Ensure only the employer who posted the job can view applicants
        if (!job.getEmployer().getId().equals(employer.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only view applicants for your own jobs.");
        }

        return ResponseEntity.ok(applicationRepository.findByJob(job));
    }
}

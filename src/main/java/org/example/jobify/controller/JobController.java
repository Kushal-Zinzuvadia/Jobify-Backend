package org.example.jobify.controller;

import org.example.jobify.model.Job;
import org.example.jobify.model.User;
import org.example.jobify.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/jobs")
@CrossOrigin(origins = "http://localhost:5173")
public class JobController {

    @Autowired
    private JobService jobService;

    @PostMapping()
    public ResponseEntity<Job> createJob(@RequestBody Job job) {
        System.out.println("Creating job");
        Job createdJob = jobService.saveJob(job);
        System.out.println("Generated Job ID: " + createdJob.getId());
        return ResponseEntity.ok(createdJob);
    }

    @PreAuthorize("hasAuthority('SCOPE_read:jobs')")
    @GetMapping()
    public ResponseEntity<Map<String, Object>> getJobs(
            @RequestParam(required = false, defaultValue = "") String searchTerm,
            @RequestParam(required = false, defaultValue = "") String location,
            @RequestParam(required = false, defaultValue = "full-time") String jobType,
            @RequestParam(required = false, defaultValue = "entry") String experience,
            @RequestParam(required = false, defaultValue = "") String salaryRange,
            @RequestParam(required = false, defaultValue = "1") int page
    ) {
        List<Job> jobs = jobService.getFilteredJobs(searchTerm, location, jobType, experience, salaryRange, page);

        Map<String, Object> response = new HashMap<>();
        response.put("jobs", jobs);
        response.put("total", jobs.size());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{jobId}/applicants")
    public List<User> getApplicantsByJob(@PathVariable UUID jobId) {
        return jobService.getApplicantsByJob(jobId);
    }

    @GetMapping("/posted/{employerId}")
    public List<Job> getPostedJobs(@PathVariable UUID employerId) {
        return jobService.getPostings(employerId);
    }
}

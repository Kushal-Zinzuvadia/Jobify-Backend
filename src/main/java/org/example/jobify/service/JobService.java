package org.example.jobify.service;

import org.example.jobify.model.Job;
import org.example.jobify.model.User;
import org.example.jobify.repository.JobRepository;
import org.example.jobify.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class JobService {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private UserRepository userRepository;

    public Job saveJob(Job job) {
        System.out.println("Saving job");

        User employer = userRepository.findById(job.getEmployerId())
                .orElseThrow(() -> new RuntimeException("Employer not found with ID: " + job.getEmployerId()));

        System.out.println(job.getEmployerId());

        Job savedJob = jobRepository.save(job);

        System.out.println("Saved Job ID: " + savedJob.getId());

        return savedJob;
    }

    public List<Job> getFilteredJobs(String searchTerm, String location, String jobType, String experience, String salaryRange, int page) {
        return jobRepository.findAll();
    }

    // 4. Get applicants for a job
    public List<User> getApplicantsByJob(UUID jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));
        return job.getUsers();
    }

    // 7. Get all jobs
    public List<Job> getAllJobs() {
        return jobRepository.findAll();
    }

    // 8. Get job by ID
    public Job getJobById(UUID jobId) {
        return jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));
    }

    // 9. Create a job
    public Job createJob(Job job) {
        return jobRepository.save(job);
    }

    // 10. Delete a job
    public void deleteJob(UUID jobId) {
        jobRepository.deleteById(jobId);
    }

    public List<Job> getPostings(UUID employerId) {
        return jobRepository.findByEmployerId(employerId);
    }
}
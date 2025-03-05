package org.example.jobify.service;

import org.example.jobify.model.Job;
import org.example.jobify.model.User;
import org.example.jobify.repository.JobRepository;
import org.example.jobify.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobService {

    @Autowired
    private JobRepository jobRepository;
    @Autowired
    private UserRepository userRepository;

    public Job saveJob(Job job) {
        System.out.println("Saving job");

        // Ensure employer exists
        User employer = userRepository.findById(job.getEmployerId())
                .orElseThrow(() -> new RuntimeException("Employer not found with ID: " + job.getEmployerId()));

        System.out.println(job.getEmployerId());

        // Save job entity
        Job savedJob = jobRepository.save(job); // ID gets generated here

        System.out.println("Saved Job ID: " + savedJob.getId()); // Now it will have a value

        return savedJob;
    }



    public List<Job> getFilteredJobs(String searchTerm, String location, String jobType, String experience, String salaryRange, int page) {
        // TODO: Add filtering logic (search by criteria from DB)
        return jobRepository.findAll(); // Replace this with actual filtered query
    }
}
package org.example.jobify.service;

import org.example.jobify.model.Job;
import org.example.jobify.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobService {

    @Autowired
    private JobRepository jobRepository;

    public Job saveJob(Job job) {
        return jobRepository.save(job);
    }

    public List<Job> getFilteredJobs(String searchTerm, String location, String jobType, String experience, String salaryRange, int page) {
        // TODO: Add filtering logic (search by criteria from DB)
        return jobRepository.findAll(); // Replace this with actual filtered query
    }
}
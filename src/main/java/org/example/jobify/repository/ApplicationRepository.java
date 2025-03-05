package org.example.jobify.repository;

import org.example.jobify.model.Application;
import org.example.jobify.model.Job;
import org.example.jobify.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, UUID> {
    List<Application> findByUser(User user);
    List<Application> findByJob(Job job);

    List<Application> findByJobId(UUID jobId);

    List<Application> findByUserEmail(String email);
}


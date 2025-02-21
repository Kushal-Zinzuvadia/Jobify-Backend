package org.example.jobify.repository;

import org.example.jobify.model.Job;
import org.example.jobify.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface JobRepository extends JpaRepository<Job, UUID> {
    List<Job> findByEmployer(User employer);
}

package org.example.jobify.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Setter
@Getter
@Entity
public class Job {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    private String jobTitle;
    private String company;
    private String location;
    private String salary;
    private String jobType;
    private String experience;
    private String description;
    private String requirements;

    @Column(name = "employer_id", unique = false, nullable = false)
    private UUID employerId;

    @ManyToMany(mappedBy = "jobs")
    private List<User> users = new ArrayList<>();
}

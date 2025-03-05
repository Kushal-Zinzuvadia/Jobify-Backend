package org.example.jobify.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

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
}

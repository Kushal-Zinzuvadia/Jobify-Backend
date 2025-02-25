package org.example.jobify.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    @Column(unique = true, nullable = false)
    @Email(message = "Enter a valid email")
    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotNull(message = "Password can't be null")
    private String password;

    @Enumerated(EnumType.STRING)
    private RoleName roleName; // JOB_SEEKER, EMPLOYER

    @CreationTimestamp
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "employer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Job> jobs;

    @JsonIgnore
    private String verificationToken;  // To store the email verification token

    @JsonIgnore
    private boolean verified;  // To check if the user is verified

    @Setter
    @Getter
    private boolean enabled;

}

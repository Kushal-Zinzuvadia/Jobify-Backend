package org.example.jobify.repository;

import org.example.jobify.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MyUserDetailsRepository extends CrudRepository<User,String> {

   public Optional<User> findByEmail(String email);

   Optional<User> findByVerificationToken(String token);

    boolean existsByEmail(String email);
}

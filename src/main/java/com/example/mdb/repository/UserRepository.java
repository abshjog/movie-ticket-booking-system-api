package com.example.mdb.repository;

import com.example.mdb.entity.UserDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserDetails, String> {

    boolean existsByEmail(String email);
    UserDetails findByEmail(String email);
    // Optional<UserDetails> findByEmail(String email);
}

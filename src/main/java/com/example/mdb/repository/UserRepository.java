package com.example.mdb.repository;

import com.example.mdb.entity.UserDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserDetails, String> {

    boolean existsByEmail(String email);
}

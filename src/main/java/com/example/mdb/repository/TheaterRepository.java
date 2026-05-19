package com.example.mdb.repository;

import com.example.mdb.entity.Theater;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TheaterRepository extends JpaRepository<Theater, String> {

    boolean existsByNameAndAddressAndCity(String name, String address, String city);

    List<Theater> findByTheaterOwner_Email(String email);
}

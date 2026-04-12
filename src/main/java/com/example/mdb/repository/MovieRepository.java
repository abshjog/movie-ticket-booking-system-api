package com.example.mdb.repository;

import com.example.mdb.entity.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, String> {

    Page<Movie> findByTitleContainingIgnoreCase(String search, Pageable pageable);
}

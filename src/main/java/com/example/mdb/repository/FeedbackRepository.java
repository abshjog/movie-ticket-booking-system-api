package com.example.mdb.repository;

import com.example.mdb.entity.Feedback;
import com.example.mdb.entity.Movie;
import com.example.mdb.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackRepository extends JpaRepository<Feedback, String> {

    boolean existsByMovieAndUser(Movie movie, User user);
}

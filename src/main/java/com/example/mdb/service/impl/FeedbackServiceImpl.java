package com.example.mdb.service.impl;

import com.example.mdb.dto.FeedbackRequest;
import com.example.mdb.dto.FeedbackResponse;
import com.example.mdb.entity.Feedback;
import com.example.mdb.entity.Movie;
import com.example.mdb.entity.User;
import com.example.mdb.exception.MovieNotFoundException;
import com.example.mdb.exception.UserNotFoundException;
import com.example.mdb.mapper.FeedbackMapper;
import com.example.mdb.repository.FeedbackRepository;
import com.example.mdb.repository.MovieRepository;
import com.example.mdb.repository.UserRepository;
import com.example.mdb.service.FeedbackService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class FeedbackServiceImpl implements FeedbackService {

    private final MovieRepository movieRepository;
    private final UserRepository userRepository;
    private final FeedbackRepository feedbackRepository;
    private final FeedbackMapper feedbackMapper;

    @Override
    public FeedbackResponse createFeedback(String movieId, FeedbackRequest feedbackRequest, String email) {

        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new MovieNotFoundException("Movie with the provided ID is not found."));

        User user = (User) userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        if (feedbackRepository.existsByMovieAndUser(movie, user)) {
            throw new IllegalStateException("You have already reviewed this movie!");
        }

        Feedback feedback = new Feedback();
        feedback.setRating(feedbackRequest.rating());
        feedback.setReview(feedbackRequest.review());
        feedback.setMovie(movie);
        feedback.setUser(user);

        feedbackRepository.save(feedback);
        return feedbackMapper.feedbackResponseMapper(feedback);
    }
}

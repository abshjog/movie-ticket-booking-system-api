package com.example.mdb.service.impl;

import com.example.mdb.dto.FeedbackRequest;
import com.example.mdb.dto.FeedbackResponse;
import com.example.mdb.entity.Feedback;
import com.example.mdb.entity.User;
import com.example.mdb.exception.MovieNotFoundByIdException;
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
        if(movieRepository.existsById(movieId)){
            Feedback feedback = copy(feedbackRequest, new Feedback(), movieId, email);

            return feedbackMapper.feedbackResponseMapper(feedback);
        }
        throw new MovieNotFoundByIdException("No movie found in the database");
    }

    private Feedback copy(FeedbackRequest feedbackRequest, Feedback feedback, String movieId, String email) {
        feedback.setRating(feedbackRequest.rating());
        feedback.setReview(feedbackRequest.review());
        feedback.setMovie(movieRepository.findById(movieId).get());
        feedback.setUser((User) userRepository.findByEmail(email));
        feedbackRepository.save(feedback);
        return feedback;
    }
}

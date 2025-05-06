package com.example.mdb.service;

import com.example.mdb.dto.FeedbackRequest;
import com.example.mdb.dto.FeedbackResponse;
import jakarta.validation.Valid;

public interface FeedbackService {

    FeedbackResponse createFeedback(String movieId, @Valid FeedbackRequest feedbackRequest, String email);
}

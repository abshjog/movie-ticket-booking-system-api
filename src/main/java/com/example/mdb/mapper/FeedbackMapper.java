package com.example.mdb.mapper;

import com.example.mdb.dto.FeedbackResponse;
import com.example.mdb.entity.Feedback;
import org.springframework.stereotype.Component;

@Component
public class FeedbackMapper {

    public FeedbackResponse feedbackResponseMapper(Feedback feedback) {
        if (feedback == null) {
            return null;
        }
        return new FeedbackResponse(
                feedback.getFeedbackId(),
                feedback.getRating(),
                feedback.getReview()
        );
    }
}

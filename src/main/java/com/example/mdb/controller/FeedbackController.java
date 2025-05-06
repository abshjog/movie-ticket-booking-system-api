package com.example.mdb.controller;

import com.example.mdb.dto.FeedbackRequest;
import com.example.mdb.dto.FeedbackResponse;
import com.example.mdb.service.FeedbackService;
import com.example.mdb.utility.ResponseStructure;
import com.example.mdb.utility.RestResponseBuilder;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;
    private final RestResponseBuilder responseBuilder;

    @PostMapping("/movies/{movieId}/feedbacks")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<ResponseStructure<FeedbackResponse>> createFeedback (@PathVariable String movieId, @RequestBody @Valid FeedbackRequest feedbackRequest){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        FeedbackResponse feedbackResponse = feedbackService.createFeedback(movieId, feedbackRequest, email);
        return responseBuilder.success(HttpStatus.OK, "Feedback successfully saved", feedbackResponse);
    }
}

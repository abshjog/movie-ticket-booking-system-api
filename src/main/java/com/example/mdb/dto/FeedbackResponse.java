package com.example.mdb.dto;

import lombok.Builder;

@Builder
public record FeedbackResponse(

        String feedbackId,
        int rating,
        String review
) {}

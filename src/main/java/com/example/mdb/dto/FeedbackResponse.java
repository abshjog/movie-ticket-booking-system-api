package com.example.mdb.dto;

public record FeedbackResponse(
        String feedbackId,
        int rating,
        String review
) {}

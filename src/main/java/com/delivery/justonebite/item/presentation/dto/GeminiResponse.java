package com.delivery.justonebite.item.presentation.dto;

import java.util.List;

public record GeminiResponse(
    List<Candidate> candidates,
    PromptFeedback promptFeedback,
    UsageMetadata usageMetadata,
    String modelVersion,
    String responseId
) {
    public record Candidate(
        Content content
    ) {}

    public record Content(
        List<Part> parts
    ) {}

    public record Part(
        String text
    ) {}

    public record PromptFeedback() {};

    public record UsageMetadata() {};
}
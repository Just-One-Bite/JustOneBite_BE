package com.delivery.justonebite.item.infrastructure.api.gemini.client;

import com.delivery.justonebite.global.exception.custom.CustomException;
import com.delivery.justonebite.global.exception.response.ErrorCode;
import com.delivery.justonebite.item.infrastructure.api.gemini.dto.GeminiRequestContent;
import com.delivery.justonebite.item.infrastructure.api.gemini.dto.GeminiRequestPart;
import com.delivery.justonebite.item.infrastructure.api.gemini.dto.GeminiRequestTextPart;
import com.delivery.justonebite.item.presentation.dto.GeminiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;

@Component
public class GeminiClient {
    private final RestTemplate restTemplate;

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;


    public GeminiClient(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    public String generateAiResponse(String prompt) {
        HttpHeaders headers = new HttpHeaders();

        headers.add("x-goog-api-key", apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        GeminiRequestTextPart text = new GeminiRequestTextPart(prompt);
        GeminiRequestPart parts = new GeminiRequestPart(List.of(text));
        GeminiRequestContent contents = new GeminiRequestContent(List.of(parts));

        HttpEntity<GeminiRequestContent> entity = new HttpEntity<>(contents, headers);

        GeminiResponse response = restTemplate.exchange(
            apiUrl,
            HttpMethod.POST,
            entity,
            GeminiResponse.class
        ).getBody();

        try {
            return Objects.requireNonNull(response).candidates()
                .getFirst()
                .content()
                .parts()
                .getFirst()
                .text();
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INVALID_AI_RESPONSE);
        }
    }
}

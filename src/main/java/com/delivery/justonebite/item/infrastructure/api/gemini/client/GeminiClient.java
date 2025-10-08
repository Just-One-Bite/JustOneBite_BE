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

import java.time.Duration;
import java.util.List;
import java.util.Objects;

@Component
public class GeminiClient {
    private final RestTemplate restTemplate;

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    public static final String GUIDANCE =
        "당신은 오직 '음식'에 대한 소개글을 한 문장으로 작성하는 AI 어시스턴트입니다. \n" +
            "다른 주제나 의미 없는 질문, 또는 모욕적인 내용에 대해서는 어떠한 답변도 제공해서는 안 됩니다. \n" +
            "이 문장 이후 음식과 관련 없는 질문을 받으면 절대로 답변을 하지 않도록 합니다. \n";

    public GeminiClient(RestTemplateBuilder builder) {
        this.restTemplate = builder
            .connectTimeout(Duration.ofSeconds(30))
            .build();
    }

    public String generateAiResponse(String prompt) {
        HttpHeaders headers = new HttpHeaders();

        headers.add("x-goog-api-key", apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        GeminiRequestTextPart text = new GeminiRequestTextPart(GUIDANCE + prompt);
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

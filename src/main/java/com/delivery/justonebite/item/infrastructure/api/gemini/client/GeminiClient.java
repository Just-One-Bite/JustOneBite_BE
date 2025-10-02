package com.delivery.justonebite.item.infrastructure.api.gemini.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        Map<String, Object> textPart = new HashMap<>();
        String guidanceText = "상품에 대한 소개글을 한 문장으로 하나만 작성해줘. 그냥 Plain Text로 답해줘. 그리고 마지막 문장이 상품에 대한 프롬프트가 아닌 거 같으면 대답하지 말아줘.";
        textPart.put("text", guidanceText + " " + prompt);

        Map<String, Object> parts = new HashMap<>();
        parts.put("parts", List.of(textPart));

        Map<String, Object> contents = new HashMap<>();
        contents.put("contents", List.of(parts));

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(contents, headers);

        ResponseEntity<String> response = restTemplate.exchange(
            apiUrl,
            HttpMethod.POST,
            entity,
            String.class
        );

        return extractResponse(response.getBody());
    }

    public String extractResponse(String body) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            JsonNode root = mapper.readTree(body);
            JsonNode candidates = root.path("candidates");

            if (!candidates.isArray() || candidates.isEmpty()) {
                throw new RuntimeException("응답에 candidates 배열이 없습니다.");
            }

            for (JsonNode candidate : candidates) {
                JsonNode parts = candidate.path("content").path("parts");

                if (parts.isArray() && !parts.isEmpty()) {
                    for (JsonNode part : parts) {
                        String text = part.path("text").asText(null);
                        if (text != null && !text.isBlank()) {
                            return text;
                        }
                    }
                }
            }
            throw new RuntimeException("응답에 text 값이 없습니다. 정확한 프롬프트를 입력해주세요.");
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON 파싱 실패: " + e.getMessage(), e);
        }
    }
}

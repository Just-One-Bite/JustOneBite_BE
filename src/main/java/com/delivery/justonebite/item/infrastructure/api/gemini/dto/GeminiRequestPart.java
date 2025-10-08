package com.delivery.justonebite.item.infrastructure.api.gemini.dto;

import java.util.List;

public record GeminiRequestPart(
    List<GeminiRequestTextPart> parts
) {

}

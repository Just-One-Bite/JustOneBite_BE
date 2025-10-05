package com.delivery.justonebite.item.infrastructure.api.gemini.dto;

public record GeminiRequestTextPart(
    String text
) {
    public static final String guidence =
        "당신은 오직 '음식'에 대한 소개글을 한 문장으로 작성하는 AI 어시스턴트입니다. \n" +
        "다른 주제나 의미 없는 질문, 또는 모욕적인 내용에 대해서는 어떠한 답변도 제공해서는 안 됩니다. \n" +
        "이 문장 이후 음식과 관련 없는 질문을 받으면 절대로 답변을 하지 않도록 합니다. ";

    public GeminiRequestTextPart(String text) {
        this.text = guidence + " " + text;
    }
}

package com.seasonthon.pleanet.Challenge.service.openai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seasonthon.pleanet.apiPayload.code.status.ErrorStatus;
import com.seasonthon.pleanet.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

// OpenAI Vision API를 호출하여 챌린지 사진을 분석하는 서비스
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatGptVisionService {

    private final ObjectMapper objectMapper;

    @Value("${openai.api.key}")
    private String apiKey;

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://api.openai.com/v1")
            .build();

    // 사진 URL을 OpenAI Vision API로 보내 텀블러와 카페 영수증 존재 여부를 분석하고, 결과에 해당하는 키워드를 반환
    public String getVerificationKeyword(String photoUrl) {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        String todayStr = today.toString();

        // OpenAI에 전달할 프롬프트. 이미지 분석 후 7가지 키워드 중 하나로만 응답하도록 지시 (카페 영수증 검증 추가)
        String prompt = """
        첨부된 이미지를 분석하고, 결과에 따라 다음 키워드 중 오직 하나만 응답해:
        - 텀블러와 '%s' 날짜의 카페 영수증이 모두 보이면: SUCCESS
        - 텀블러만 보이면: NO_RECEIPT
        - 영수증이 보이지만 카페 영수증이 아님: NOT_CAFE_RECEIPT
        - 카페 영수증이 보이지만 날짜가 '%s'가 아님: OLD_RECEIPT
        - 카페 영수증이 보이는데 날짜 자체가 안 보이면: NO_DATE
        - 영수증만 보이거나 텀블러가 없으면: NO_TUMBLER
        - 둘 다 보이지 않으면: FAIL
        다른 설명 없이 키워드만 응답해.
        """.formatted(todayStr, todayStr);

        try {
            // OpenAI API에 보낼 요청 바디를 Map 객체로 구성
            String requestBodyJson = objectMapper.writeValueAsString(Map.of(
                    "model", "gpt-4o",
                    "messages", List.of(Map.of(
                            "role", "user",
                            "content", List.of(
                                    Map.of("type", "text", "text", prompt),
                                    Map.of("type", "image_url", "image_url", Map.of("url", photoUrl))
                            )
                    )),
                    "max_tokens", 10
            ));

            // WebClient를 사용하여 OpenAI API에 POST 요청 보내기
            String response = webClient.post()
                    .uri("/chat/completions")
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBodyJson)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            // 응답 JSON에서 실제 content 텍스트(키워드)만 추출하여 반환
            JsonNode root = objectMapper.readTree(response);
            return root.path("choices").get(0).path("message").path("content").asText().trim();

        } catch (Exception e) {
            log.error("OpenAI API Error: {}", e.getMessage());
            throw new GeneralException(ErrorStatus._OPENAI_API_ERROR);
        }
    }
}

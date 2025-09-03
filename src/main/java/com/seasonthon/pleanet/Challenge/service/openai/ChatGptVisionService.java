package com.seasonthon.pleanet.Challenge.service.openai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class ChatGptVisionService {

    private final ObjectMapper objectMapper;

    @Value("${openai.api.key}")
    private String apiKey;

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://api.openai.com/v1")
            .build();

    public boolean verifyTumbler(String photoUrl) {
        String prompt = "이 사진에 텀블러(재사용 컵)가 보이면 'yes', 아니면 'no'로만 답해.";

        try {
            String response = webClient.post()
                    .uri("/chat/completions")
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .bodyValue("""
                        {
                          "model": "gpt-4o-mini",
                          "messages": [
                            {"role": "user", "content": [
                                {"type": "text", "text": "%s"},
                                {"type": "image_url", "image_url": {"url": "%s"}}
                              ]
                            }
                          ]
                        }
                        """.formatted(prompt, photoUrl))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonNode root = objectMapper.readTree(response);
            String answer = root.path("choices").get(0).path("message").path("content").asText();

            return answer.toLowerCase().contains("yes");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

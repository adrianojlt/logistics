package com.adrianojlt.logistics.ai.service;

import com.adrianojlt.logistics.ai.config.LlmConfig;
import com.adrianojlt.logistics.ai.config.ProviderConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LlmService {

    private final LlmConfig llmConfig;
    private final RestTemplate restTemplate;

    public String chat(String systemPrompt, String userMessage) {

        ProviderConfig provider = llmConfig.getActiveProviderConfig();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(provider.getApiKey());

        Map<String, Object> body = Map.of(
            "model", provider.getModel(),
            "messages", List.of(
                Map.of("role", "system", "content", systemPrompt),
                Map.of("role", "user", "content", userMessage)
            ),
            "temperature", 0.1,
            "max_tokens", 1024
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response;
        try {
            response = restTemplate.postForEntity(provider.getUrl(), request, Map.class);
        } catch (HttpClientErrorException e) {
            throw new IllegalStateException("LLM provider rejected the request (HTTP " + e.getStatusCode() + ")", e);
        } catch (HttpServerErrorException e) {
            throw new IllegalStateException("LLM provider returned a server error (HTTP " + e.getStatusCode() + ")", e);
        }

        if (response.getBody() == null) {
            throw new IllegalStateException("LLM provider returned an empty response body");
        }

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");

        if (choices == null || choices.isEmpty()) {
            throw new IllegalStateException("LLM provider response contained no choices");
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");

        if (message == null || message.get("content") == null) {
            throw new IllegalStateException("LLM provider response message content is missing");
        }

        return message.get("content").toString().trim();
    }
}

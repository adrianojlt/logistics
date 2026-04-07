package com.adrianojlt.logistics.ai.controller;

import com.adrianojlt.logistics.ai.dto.AiQueryRequestDTO;
import com.adrianojlt.logistics.ai.dto.AiQueryResponseDTO;
import com.adrianojlt.logistics.ai.service.AiQueryService;
import com.adrianojlt.logistics.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ai")
public class AiQueryController {

    private final AiQueryService aiQueryService;

    @PostMapping("/query")
    public ResponseEntity<ApiResponse<AiQueryResponseDTO>> query(@Valid @RequestBody AiQueryRequestDTO request) {
        AiQueryResponseDTO response = aiQueryService.query(request.getQuestion());
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}

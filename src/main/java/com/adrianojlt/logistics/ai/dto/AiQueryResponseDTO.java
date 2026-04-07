package com.adrianojlt.logistics.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiQueryResponseDTO {
    private String question;
    private String generatedSql;
    private String answer;
    private boolean success;
    private String errorMessage;
}

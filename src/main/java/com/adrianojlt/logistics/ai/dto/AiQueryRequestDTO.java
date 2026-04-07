package com.adrianojlt.logistics.ai.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiQueryRequestDTO {
    @NotBlank(message = "Question is required")
    private String question;
}

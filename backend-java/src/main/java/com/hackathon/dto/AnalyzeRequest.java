package com.hackathon.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AnalyzeRequest {
    @NotBlank(message = "input is required")
    private String input;
    private String context;
}

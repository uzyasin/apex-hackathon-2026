package com.hackathon.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AnalyzeRequest {
    @NotBlank(message = "input gereklidir")
    @Size(max = 10000, message = "input en fazla 10000 karakter olabilir")
    private String input;

    private String context;
}

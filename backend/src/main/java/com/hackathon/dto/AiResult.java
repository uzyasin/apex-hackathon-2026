package com.hackathon.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AiResult {
    private String summary;
    private List<String> insights;
    private int score;
    private String recommendation;

    public static AiResult fallback() {
        AiResult r = new AiResult();
        r.setSummary("Analysis temporarily unavailable. Please try again.");
        r.setInsights(List.of());
        r.setScore(0);
        r.setRecommendation("Retry in a few seconds.");
        return r;
    }
}

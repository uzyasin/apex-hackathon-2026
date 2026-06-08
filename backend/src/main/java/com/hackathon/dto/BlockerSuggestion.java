package com.hackathon.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * POST /api/planning/blockers yanıtı (BONUS).
 * Bloklanmış bir task için AI destekli kök neden + çözüm önerisi.
 * Mock: contract/mocks/planning-blockers.json
 */
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class BlockerSuggestion {

    /** Jira issue key */
    private String key;

    /** Kök neden analizi (Türkçe) */
    private String rootCause;

    /** Adım adım çözüm önerileri (2-4 madde) */
    private List<String> suggestions;

    /** En öncelikli önerilen eylem */
    private String recommendedAction;

    public static BlockerSuggestion fallback(String key) {
        BlockerSuggestion b = new BlockerSuggestion();
        b.setKey(key);
        b.setRootCause("Blokaj analizi şu an yapılamıyor.");
        b.setSuggestions(List.of("Blokajı takım ile Daily Scrum'da ele alın.",
                "Birkaç saniye sonra tekrar deneyin."));
        b.setRecommendedAction("Manuel olarak blokaj sahibini belirleyin.");
        return b;
    }
}


package com.hackathon.controller;

import com.hackathon.dto.ApiResponse;
import com.hackathon.dto.IssueDto;
import com.hackathon.dto.SprintDto;
import com.hackathon.service.JiraGateway;
import com.hackathon.service.JiraService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Jira READ-ONLY endpoint'leri.
 * Tüm yanıtlar ApiResponse<T> ile sarılır.
 */
@RestController
@RequestMapping("/api/jira")
public class JiraController {

    private final JiraGateway jiraGateway;

    public JiraController(JiraGateway jiraGateway) {
        this.jiraGateway = jiraGateway;
    }

    /** GET /api/jira/sprints — board'daki tüm sprint'ler */
    @GetMapping("/sprints")
    public ResponseEntity<ApiResponse<List<SprintDto>>> getSprints() {
        return ResponseEntity.ok(ApiResponse.ok(jiraGateway.getSprints()));
    }

    /** GET /api/jira/backlog — sprint'e atanmamış backlog issue'ları */
    @GetMapping("/backlog")
    public ResponseEntity<ApiResponse<List<IssueDto>>> getBacklog() {
        return ResponseEntity.ok(ApiResponse.ok(jiraGateway.getBacklog()));
    }

    /** GET /api/jira/sprints/{sprintId}/issues — sprint içi tüm issue'lar */
    @GetMapping("/sprints/{sprintId}/issues")
    public ResponseEntity<ApiResponse<List<IssueDto>>> getSprintIssues(
            @PathVariable int sprintId) {
        return ResponseEntity.ok(ApiResponse.ok(jiraGateway.getSprintIssues(sprintId)));
    }

    /**
     * GET /api/jira/fields — "story" veya "point" içeren Jira field'larını listeler.
     * Gerçek Jira'da doğru story-point field adını keşfetmek için kullanılır.
     * Örnek: curl http://localhost:3001/api/jira/fields
     */
    @GetMapping("/fields")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> discoverFields() {
        if (jiraGateway instanceof JiraService js) {
            return ResponseEntity.ok(ApiResponse.ok(js.discoverStoryPointFields()));
        }
        return ResponseEntity.ok(ApiResponse.ok(List.of(
                Map.of("info", "Mock profilde çalışıyor — gerçek Jira bağlantısı yok")
        )));
    }
}

package com.hackathon.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hackathon.dto.IssueDto;
import com.hackathon.dto.SprintDto;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;

/**
 * Jira erişimi yokken kullanılan mock implementasyon.
 * contract/mocks/ ile birebir aynı JSON'ları (resources/mocks/) HTTP endpoint'lerinden
 * doğrudan döner. Sadece "mock" profili aktifken yüklenir.
 * READ-ONLY — hiçbir yazma işlemi yapmaz.
 */
@Service
@Profile("mock")
public class MockJiraService implements JiraGateway {

    private final ObjectMapper objectMapper;

    public MockJiraService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public List<SprintDto> getSprints() {
        return readList("mocks/jira-sprints.json", SprintDto.class);
    }

    @Override
    public List<IssueDto> getBacklog() {
        return readList("mocks/jira-backlog.json", IssueDto.class);
    }

    @Override
    public List<IssueDto> getSprintIssues(int sprintId) {
        return readList("mocks/jira-sprint-issues.json", IssueDto.class);
    }

    @Override
    public IssueDto getIssue(String issueKey) {
        if (issueKey == null) return null;
        // Hem backlog hem sprint issue'ları içinde key ile ara.
        return java.util.stream.Stream
                .concat(getBacklog().stream(), getSprintIssues(0).stream())
                .filter(i -> issueKey.equalsIgnoreCase(i.getKey()))
                .findFirst()
                .orElse(null);
    }

    // ═══════════════════════════════════════════════════════════════════
    //  PRIVATE HELPERS
    // ═══════════════════════════════════════════════════════════════════

    /** Classpath JSON dizisini DTO listesine map eder; hata olursa boş liste (fallback). */
    private <T> List<T> readList(String resourcePath, Class<T> type) {
        try (InputStream in = new ClassPathResource(resourcePath).getInputStream()) {
            return objectMapper.readValue(
                    in,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, type));
        } catch (Exception e) {
            System.err.println("[MockJiraService] readList(" + resourcePath + ") error: " + e.getMessage());
            return List.of();
        }
    }
}


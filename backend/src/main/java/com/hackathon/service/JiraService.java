package com.hackathon.service;

import com.hackathon.config.JiraConfig;
import com.hackathon.dto.IssueDto;
import com.hackathon.dto.SprintDto;
import org.springframework.context.annotation.Profile;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Jira READ-ONLY servis — hiçbir yazma işlemi yapmaz.
 * Yalnızca "mock" profili KAPALI olduğunda aktif olur.
 *
 * Auth: application.yml'deki jira.auth-type değerine göre:
 *   bearer (varsayılan) → Authorization: Bearer <token>   (Jira DC 8.14+ PAT)
 *   basic               → Authorization: Basic <token>    (username:pass base64)
 */
@Service
@Profile("!mock")
public class JiraService implements JiraGateway {

    private final JiraConfig config;
    private final RestTemplate restTemplate = new RestTemplate();

    public JiraService(JiraConfig config) {
        this.config = config;
    }

    // ═══════════════════════════════════════════════════════════════════
    //  PUBLIC API
    // ═══════════════════════════════════════════════════════════════════

    @Override
    public List<SprintDto> getSprints() {
        String url = config.getBaseUrl()
                + "/rest/agile/1.0/board/" + config.getBoardId()
                + "/sprint?maxResults=50";
        try {
            ResponseEntity<Map> res = restTemplate.exchange(url, HttpMethod.GET, entity(), Map.class);
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> values = (List<Map<String, Object>>) res.getBody().get("values");
            if (values == null) return List.of();
            return values.stream().map(this::mapSprint).collect(Collectors.toList());
        } catch (HttpClientErrorException e) {
            logHttpError("getSprints", url, e);
            return List.of();
        } catch (Exception e) {
            System.err.println("[JiraService] getSprints error: " + e.getMessage());
            return List.of();
        }
    }

    @Override
    public List<IssueDto> getBacklog() {
        String url = config.getBaseUrl()
                + "/rest/agile/1.0/board/" + config.getBoardId()
                + "/backlog?maxResults=100&fields=summary,issuetype,status,priority,labels,description,"
                + config.getStoryPointField() + ",assignee,resolutiondate";
        return fetchIssues(url, "getBacklog");
    }

    @Override
    public List<IssueDto> getSprintIssues(int sprintId) {
        String url = config.getBaseUrl()
                + "/rest/agile/1.0/sprint/" + sprintId
                + "/issue?maxResults=100&fields=summary,issuetype,status,priority,labels,description,"
                + config.getStoryPointField() + ",assignee,resolutiondate";
        return fetchIssues(url, "getSprintIssues(" + sprintId + ")");
    }

    @Override
    public IssueDto getIssue(String issueKey) {
        String url = config.getBaseUrl() + "/rest/api/2/issue/" + issueKey
                + "?fields=summary,issuetype,status,priority,labels,description,"
                + config.getStoryPointField() + ",assignee,resolutiondate";
        try {
            ResponseEntity<Map> res = restTemplate.exchange(url, HttpMethod.GET, entity(), Map.class);
            return mapIssue(res.getBody());
        } catch (HttpClientErrorException e) {
            logHttpError("getIssue(" + issueKey + ")", url, e);
            return null;
        } catch (Exception e) {
            System.err.println("[JiraService] getIssue(" + issueKey + ") error: " + e.getMessage());
            return null;
        }
    }

    /**
     * Jira'daki tüm field'ları listeler — story point field adını bulmak için kullanılır.
     * JiraController üzerinden /api/jira/fields endpoint'i ile erişilir.
     * Story point içeren field adlarını ve ID'lerini döner.
     */
    public List<Map<String, Object>> discoverStoryPointFields() {
        String url = config.getBaseUrl() + "/rest/api/2/field";
        try {
            ResponseEntity<List> res = restTemplate.exchange(url, HttpMethod.GET, entity(), List.class);
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> allFields = (List<Map<String, Object>>) res.getBody();
            if (allFields == null) return List.of();
            return allFields.stream()
                    .filter(f -> {
                        String name = String.valueOf(f.get("name")).toLowerCase();
                        String id = String.valueOf(f.get("id")).toLowerCase();
                        return name.contains("story") || name.contains("point") || name.contains("sp")
                                || id.contains("story") || id.startsWith("customfield_1000");
                    })
                    .map(f -> Map.<String, Object>of(
                            "id", f.get("id"),
                            "name", f.get("name"),
                            "custom", Boolean.TRUE.equals(f.get("custom"))
                    ))
                    .collect(Collectors.toList());
        } catch (HttpClientErrorException e) {
            logHttpError("discoverStoryPointFields", url, e);
            return List.of();
        } catch (Exception e) {
            System.err.println("[JiraService] discoverStoryPointFields error: " + e.getMessage());
            return List.of();
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    //  PRIVATE HELPERS
    // ═══════════════════════════════════════════════════════════════════

    private List<IssueDto> fetchIssues(String url, String operation) {
        try {
            ResponseEntity<Map> res = restTemplate.exchange(url, HttpMethod.GET, entity(), Map.class);
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> issues = (List<Map<String, Object>>) res.getBody().get("issues");
            if (issues == null) return List.of();
            return issues.stream().map(this::mapIssue).collect(Collectors.toList());
        } catch (HttpClientErrorException e) {
            logHttpError(operation, url, e);
            return List.of();
        } catch (Exception e) {
            System.err.println("[JiraService] " + operation + " error: " + e.getMessage());
            return List.of();
        }
    }

    private HttpEntity<Void> entity() {
        HttpHeaders headers = new HttpHeaders();
        String authType = config.getAuthType() == null ? "bearer" : config.getAuthType().toLowerCase();
        if ("basic".equals(authType)) {
            headers.set("Authorization", "Basic " + config.getToken());
        } else {
            // bearer — Jira Data Center PAT (Personal Access Token)
            headers.set("Authorization", "Bearer " + config.getToken());
        }
        headers.set("Accept", "application/json");
        return new HttpEntity<>(headers);
    }

    private void logHttpError(String operation, String url, HttpClientErrorException e) {
        System.err.println("[JiraService] " + operation + " HTTP " + e.getStatusCode() + " → " + url);
        String body = e.getResponseBodyAsString();
        if (body != null && body.length() > 300) body = body.substring(0, 300) + "...";
        System.err.println("[JiraService] Response: " + body);
        if (e.getStatusCode().value() == 401) {
            System.err.println("[JiraService] 401 ipuçları:");
            System.err.println("  • jira.auth-type=" + config.getAuthType() + " — 'bearer' veya 'basic' deneyin");
            System.err.println("  • Token doğru mu? application.yml → jira.token");
            System.err.println("  • Bearer için: Jira profil ayarlarından PAT (Personal Access Token) oluşturun");
        }
    }

    private SprintDto mapSprint(Map<String, Object> raw) {
        SprintDto dto = new SprintDto();
        Object idObj = raw.get("id");
        if (idObj != null) dto.setId(((Number) idObj).intValue());
        dto.setName(str(raw, "name"));
        dto.setState(str(raw, "state"));
        dto.setStartDate(str(raw, "startDate"));
        dto.setEndDate(str(raw, "endDate"));
        dto.setGoal(str(raw, "goal"));
        return dto;
    }

    @SuppressWarnings("unchecked")
    private IssueDto mapIssue(Map<String, Object> raw) {
        IssueDto dto = new IssueDto();
        dto.setKey(str(raw, "key"));

        Map<String, Object> fields = (Map<String, Object>) raw.get("fields");
        if (fields == null) return dto;

        dto.setSummary(str(fields, "summary"));
        dto.setDescription(str(fields, "description"));

        Map<String, Object> issueType = (Map<String, Object>) fields.get("issuetype");
        if (issueType != null) dto.setIssueType(str(issueType, "name"));

        Map<String, Object> status = (Map<String, Object>) fields.get("status");
        if (status != null) {
            dto.setStatus(str(status, "name"));
            Map<String, Object> cat = (Map<String, Object>) status.get("statusCategory");
            if (cat != null) dto.setStatusCategory(toStatusCategory(str(cat, "key")));
        }

        // Story point: önce config'deki field'ı dene, yoksa yaygın alternatifleri
        Object sp = fields.get(config.getStoryPointField());
        if (sp == null) sp = fields.get("story_points");
        if (sp == null) sp = fields.get("customfield_10016"); // Jira Cloud / bazı DC kurulumları
        if (sp == null) sp = fields.get("customfield_10028"); // diğer yaygın field
        if (sp != null) {
            try { dto.setStoryPoints(((Number) sp).intValue()); }
            catch (Exception ignored) {}
        }

        Map<String, Object> assignee = (Map<String, Object>) fields.get("assignee");
        if (assignee != null) {
            String name = str(assignee, "displayName");
            if (name == null) name = str(assignee, "name");
            dto.setAssignee(name);
        }

        Map<String, Object> priority = (Map<String, Object>) fields.get("priority");
        if (priority != null) dto.setPriority(str(priority, "name"));

        Object labelsRaw = fields.get("labels");
        if (labelsRaw instanceof List) dto.setLabels((List<String>) labelsRaw);

        String resDate = str(fields, "resolutiondate");
        if (resDate != null && !resDate.isBlank()) {
            dto.setResolved(true);
            dto.setResolvedDate(resDate.length() >= 10 ? resDate.substring(0, 10) : resDate);
        }

        return dto;
    }

    private static String toStatusCategory(String jiraKey) {
        if (jiraKey == null) return "TODO";
        return switch (jiraKey) {
            case "new" -> "TODO";
            case "indeterminate" -> "IN_PROGRESS";
            case "done" -> "DONE";
            default -> "TODO";
        };
    }

    private static String str(Map<String, Object> map, String key) {
        Object v = map.get(key);
        return v instanceof String s ? s : null;
    }
}

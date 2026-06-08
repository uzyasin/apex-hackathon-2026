package com.hackathon.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Jira issue. Hem backlog hem sprint issue yanıtında kullanılır.
 * Sprint issue'larında resolved + resolvedDate ek alanları dolu gelir.
 * Mock: contract/mocks/jira-backlog.json, contract/mocks/jira-sprint-issues.json
 */
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class IssueDto {
    private String key;
    private String summary;
    /** Story | Task | Bug | Epic */
    private String issueType;
    private String status;
    /** TODO | IN_PROGRESS | DONE */
    private String statusCategory;
    private Integer storyPoints;
    private String assignee;
    private String priority;
    private List<String> labels;
    private String description;
    /** Sprint issue: tamamlandıysa true */
    private boolean resolved;
    /** Sprint issue: çözüm tarihi ISO (yyyy-MM-dd), null ise tamamlanmamış */
    private String resolvedDate;
}

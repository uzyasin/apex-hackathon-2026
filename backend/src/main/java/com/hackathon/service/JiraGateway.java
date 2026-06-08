package com.hackathon.service;

import com.hackathon.dto.IssueDto;
import com.hackathon.dto.SprintDto;

import java.util.List;

/**
 * Jira okuma katmanı sözleşmesi (READ-ONLY).
 * İki implementasyon: gerçek {@link JiraService} (@Profile "!mock")
 * ve {@link MockJiraService} (@Profile "mock").
 */
public interface JiraGateway {

    /** Board'daki tüm sprint'ler (active + closed + future). */
    List<SprintDto> getSprints();

    /** Board backlog'undaki (sprint'e atanmamış) issue'lar. */
    List<IssueDto> getBacklog();

    /** Belirtilen sprint'teki tüm issue'lar. */
    List<IssueDto> getSprintIssues(int sprintId);

    /** Tek issue detayı — bulunamazsa null. */
    IssueDto getIssue(String issueKey);
}


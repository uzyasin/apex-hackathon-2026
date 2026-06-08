package com.hackathon.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

/** GET /api/jira/sprints — tek sprint. Mock: contract/mocks/jira-sprints.json */
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SprintDto {
    private int id;
    private String name;
    /** active | closed | future */
    private String state;
    private String startDate;
    private String endDate;
    private String goal;
}

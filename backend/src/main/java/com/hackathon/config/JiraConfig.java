package com.hackathon.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class JiraConfig {

    @Value("${jira.base-url}")
    private String baseUrl;

    @Value("${jira.token}")
    private String token;

    @Value("${jira.board-id}")
    private int boardId;

    @Value("${jira.story-point-field}")
    private String storyPointField;
}

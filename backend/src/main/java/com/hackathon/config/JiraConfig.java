package com.hackathon.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Gerçek Jira bağlantı ayarları. Sadece "mock" profili aktif değilken yüklenir.
 * Boş/eksik config değerlerinde uygulama çökmesin diye güvenli varsayılanlar tanımlı.
 */
@Configuration
@Profile("!mock")
@Getter
public class JiraConfig {

    @Value("${jira.base-url:https://jira.turkcell.com.tr}")
    private String baseUrl;

    @Value("${jira.token:}")
    private String token;

    @Value("${jira.board-id:0}")
    private int boardId;

    @Value("${jira.story-point-field:customfield_10002}")
    private String storyPointField;

    /** bearer (PAT) veya basic. Default: bearer */
    @Value("${jira.auth-type:bearer}")
    private String authType;
}

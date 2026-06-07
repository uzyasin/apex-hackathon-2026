package com.hackathon.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hackathon.dto.AiResult;
import jakarta.annotation.PostConstruct;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

@Service
public class DbService {

    private final JdbcTemplate jdbc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public DbService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @PostConstruct
    public void init() {
        jdbc.execute("""
            CREATE TABLE IF NOT EXISTS analyses (
              id BIGINT AUTO_INCREMENT PRIMARY KEY,
              input CLOB NOT NULL,
              context CLOB,
              result_json CLOB,
              score INT,
              created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """);
        System.out.println("[DbService] analyses tablosu hazır");
    }

    public Long saveAnalysis(String input, String context, AiResult result) {
        try {
            String json = objectMapper.writeValueAsString(result);
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbc.update(conn -> {
                PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO analyses (input, context, result_json, score) VALUES (?,?,?,?)",
                        Statement.RETURN_GENERATED_KEYS
                );
                ps.setString(1, input);
                ps.setString(2, context);
                ps.setString(3, json);
                ps.setInt(4, result.getScore());
                return ps;
            }, keyHolder);
            return keyHolder.getKey().longValue();
        } catch (Exception e) {
            throw new RuntimeException("DB save failed: " + e.getMessage(), e);
        }
    }

    public List<Map<String, Object>> getAllAnalyses() {
        return jdbc.queryForList(
                "SELECT id, input, score, created_at FROM analyses ORDER BY created_at DESC LIMIT 100"
        );
    }

    public Map<String, Object> getAnalysisById(Long id) {
        List<Map<String, Object>> rows = jdbc.queryForList(
                "SELECT * FROM analyses WHERE id = ?", id
        );
        return rows.isEmpty() ? null : rows.get(0);
    }
}

package com.hackathon.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hackathon.dto.TeamMemberDto;
import jakarta.annotation.PostConstruct;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Map;

/**
 * Takım yetkinlik matrisi ve kapasite — H2 in-memory, seed ile dolu gelir.
 * GET /api/team
 */
@Service
public class TeamService {

    private final JdbcTemplate jdbc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public TeamService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @PostConstruct
    public void init() {
        jdbc.execute("""
            CREATE TABLE IF NOT EXISTS team_members (
              id           VARCHAR(10) PRIMARY KEY,
              name         VARCHAR(100) NOT NULL,
              skills       CLOB NOT NULL,
              capacity_hours   INT NOT NULL DEFAULT 60,
              current_load_hours INT NOT NULL DEFAULT 0
            )
        """);

        if (jdbc.queryForObject("SELECT COUNT(*) FROM team_members", Integer.class) == 0) {
            seed();
        }
    }

    public List<TeamMemberDto> getAll() {
        return jdbc.queryForList("SELECT * FROM team_members").stream()
                .map(this::map)
                .toList();
    }

    // ═══════════════════════════════════════════════════════════════════
    //  PRIVATE
    // ═══════════════════════════════════════════════════════════════════

    private void seed() {
        insert("u1", "Ali",
                Map.of("FRONTEND", 4, "BACKEND", 2, "DB", 3, "TEST", 2, "DEVOPS", 1),
                60, 18);
        insert("u2", "Veli",
                Map.of("FRONTEND", 2, "BACKEND", 5, "DB", 4, "TEST", 2, "DEVOPS", 3),
                60, 24);
        insert("u3", "Ayşe",
                Map.of("FRONTEND", 5, "BACKEND", 1, "DB", 1, "TEST", 4, "DEVOPS", 0),
                40, 8);
        insert("u4", "Can",
                Map.of("FRONTEND", 1, "BACKEND", 3, "DB", 5, "TEST", 3, "DEVOPS", 4),
                60, 30);
        insert("u5", "Zeynep",
                Map.of("FRONTEND", 3, "BACKEND", 4, "DB", 2, "TEST", 5, "DEVOPS", 2),
                40, 10);
    }

    private void insert(String id, String name, Map<String, Integer> skills,
                        int capacity, int load) {
        try {
            String skillsJson = objectMapper.writeValueAsString(skills);
            jdbc.update(conn -> {
                PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO team_members (id, name, skills, capacity_hours, current_load_hours) " +
                        "VALUES (?,?,?,?,?)");
                ps.setString(1, id);
                ps.setString(2, name);
                ps.setString(3, skillsJson);
                ps.setInt(4, capacity);
                ps.setInt(5, load);
                return ps;
            }, new GeneratedKeyHolder());
        } catch (Exception e) {
            System.err.println("[TeamService] seed insert error: " + e.getMessage());
        }
    }

    private TeamMemberDto map(Map<String, Object> row) {
        try {
            String skillsJson = (String) row.get("SKILLS");
            Map<String, Integer> skills = objectMapper.readValue(
                    skillsJson, new TypeReference<>() {});
            return new TeamMemberDto(
                    (String) row.get("ID"),
                    (String) row.get("NAME"),
                    skills,
                    ((Number) row.get("CAPACITY_HOURS")).intValue(),
                    ((Number) row.get("CURRENT_LOAD_HOURS")).intValue()
            );
        } catch (Exception e) {
            System.err.println("[TeamService] map error: " + e.getMessage());
            return new TeamMemberDto((String) row.get("ID"), (String) row.get("NAME"),
                    Map.of(), 60, 0);
        }
    }
}

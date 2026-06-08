package com.hackathon.controller;

import com.hackathon.dto.*;
import com.hackathon.service.TaskService;
import com.hackathon.service.TeamService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Epik 2 — Görev kırılımı ve akıllı atama endpoint'leri.
 */
@RestController
@RequestMapping("/api")
public class TaskController {

    private final TaskService taskService;
    private final TeamService teamService;

    public TaskController(TaskService taskService, TeamService teamService) {
        this.taskService = taskService;
        this.teamService = teamService;
    }

    /** GET /api/team — takım yetkinlik matrisi ve kapasite */
    @GetMapping("/team")
    public ResponseEntity<ApiResponse<List<TeamMemberDto>>> getTeam() {
        return ResponseEntity.ok(ApiResponse.ok(teamService.getAll()));
    }

    /**
     * POST /api/tasks/decompose
     * İstek: { "issueKey", "summary", "description", "storyPoints" }
     */
    @PostMapping("/tasks/decompose")
    public ResponseEntity<ApiResponse<DecomposeResponse>> decompose(
            @Valid @RequestBody DecomposeReq req) {
        return ResponseEntity.ok(ApiResponse.ok(
                taskService.decompose(req.issueKey(), req.summary(),
                        req.description(), req.storyPoints())));
    }

    /**
     * POST /api/tasks/assign
     * İstek: { "subtasks": [{"tempId","title","discipline","estimateHours"}] }
     */
    @PostMapping("/tasks/assign")
    public ResponseEntity<ApiResponse<AssignResponse>> assign(
            @Valid @RequestBody AssignRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(taskService.assign(req.getSubtasks())));
    }

    /**
     * POST /api/tasks/decompose-and-assign — decompose + assign tek çağrıda
     * İstek: decompose ile aynı
     */
    @PostMapping("/tasks/decompose-and-assign")
    public ResponseEntity<ApiResponse<DecomposeAndAssignResponse>> decomposeAndAssign(
            @Valid @RequestBody DecomposeReq req) {
        return ResponseEntity.ok(ApiResponse.ok(
                taskService.decomposeAndAssign(req.issueKey(), req.summary(),
                        req.description(), req.storyPoints())));
    }

    // ─── İstek kaydı ────────────────────────────────────────────────────────

    public record DecomposeReq(
            String issueKey,
            String summary,
            String description,
            Integer storyPoints) {
    }
}

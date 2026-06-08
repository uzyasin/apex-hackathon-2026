package com.hackathon.controller;

import com.hackathon.service.AiService;
import org.springframework.web.bind.annotation.*;

/**
 * AI servis bağlantı noktası.
 * Domain endpoint'leri (planning, tasks, sprint) ilgili controller'lara taşındı:
 *   - PlanningController: /api/planning/predict-size, /api/planning/blockers
 *   - TaskController:     /api/tasks/decompose, /api/tasks/assign, /api/tasks/decompose-and-assign
 *   - SprintController:   /api/sprint/{id}/review, /dashboard, /carryover, /health
 *
 * Bu sınıf artık AiService metotlarını doğrudan çağırmaz;
 * domain controller'ları kendi servislerine (PlanningService, TaskService, SprintService) delege eder.
 */
@RestController
@RequestMapping("/api")
public class AiController {

    public AiController(AiService aiService) {
        // Bean olarak tutulur; domain controller'lar AiService'i doğrudan inject eder.
    }
}


package com.hackathon.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/** GET /api/team — takım üyesi yetkinlik ve kapasite. Mock: contract/mocks/team.json */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamMemberDto {
    private String memberId;
    private String name;
    /** Disiplin → yetkinlik (0-5): FRONTEND, BACKEND, DB, TEST, DEVOPS */
    private Map<String, Integer> skills;
    private int capacityHours;
    private int currentLoadHours;
}

package MOIYS.project.TimeAttack_Runner_Backend.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import MOIYS.project.TimeAttack_Runner_Backend.dto.GhostDataResponseDto;
import MOIYS.project.TimeAttack_Runner_Backend.service.GhostDataService;

@RestController
@RequestMapping("/api/ghost")
@RequiredArgsConstructor
public class GhostDataController {
    private final GhostDataService ghostDataService;

    @GetMapping("/{recordId}")
    public GhostDataResponseDto getGhostData(@PathVariable Long recordId) {
        return ghostDataService.findGhostDataByRecordId(recordId);
    }
}
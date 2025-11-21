package MOIYS.project.TimeAttack_Runner_Backend.controller;

import java.net.URI;
import java.util.List;
import java.util.Map;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import MOIYS.project.TimeAttack_Runner_Backend.domain.Record;

import MOIYS.project.TimeAttack_Runner_Backend.dto.RecordRequestDto;
import MOIYS.project.TimeAttack_Runner_Backend.dto.RecordResponseDto;

import MOIYS.project.TimeAttack_Runner_Backend.service.RecordService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/record")
@Validated
public class RecordController {

    private final RecordService recordService;

    @PostMapping
    public ResponseEntity<RecordResponseDto> createRecord(
            @Valid @RequestBody RecordRequestDto requestDto
    ) {
        Record record = recordService.createRecord(requestDto);

        URI location = URI.create("/api/record/" + record.getId());

        RecordResponseDto responseDto = RecordResponseDto.from(record);

        return ResponseEntity.created(location).body(responseDto);
    }

    @GetMapping("/leaderboard")
    public ResponseEntity<List<RecordResponseDto>> getLeaderboard(
            @Positive(message = "top 값은 1 이상이어야 합니다.")
            @RequestParam(value = "top") int topN
    ) {

        List<RecordResponseDto> leaderboard = recordService.findTopNByRecordTimeAsc(topN);

        return ResponseEntity.ok(leaderboard);
    }
}
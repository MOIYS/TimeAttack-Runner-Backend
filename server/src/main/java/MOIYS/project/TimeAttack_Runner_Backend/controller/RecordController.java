package MOIYS.project.TimeAttack_Runner_Backend.controller;

import java.net.URI;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import MOIYS.project.TimeAttack_Runner_Backend.domain.Record;
import MOIYS.project.TimeAttack_Runner_Backend.dto.RecordRequestDto;
import MOIYS.project.TimeAttack_Runner_Backend.dto.RecordResponseDto;
import MOIYS.project.TimeAttack_Runner_Backend.service.RecordService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/record")
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
}
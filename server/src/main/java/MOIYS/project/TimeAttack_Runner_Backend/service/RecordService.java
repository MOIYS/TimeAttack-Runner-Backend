package MOIYS.project.TimeAttack_Runner_Backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

import java.util.List;

import MOIYS.project.TimeAttack_Runner_Backend.dto.CoordinateDto;
import MOIYS.project.TimeAttack_Runner_Backend.dto.RecordRequestDto;
import MOIYS.project.TimeAttack_Runner_Backend.domain.Record;
import MOIYS.project.TimeAttack_Runner_Backend.domain.GhostData;
import MOIYS.project.TimeAttack_Runner_Backend.repository.RecordRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecordService {
    private final RecordRepository recordRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public Record createRecord(RecordRequestDto requestDto) {
        String ghostDataJson = toJsonString(requestDto.ghostData());

        GhostData newGhostData = new GhostData(ghostDataJson);
        Record newRecord = new Record(requestDto.username(), requestDto.recordTime());

        newRecord.setGhostData(newGhostData);

        return recordRepository.save(newRecord);
    }

    private String toJsonString(List<CoordinateDto> coordinates) {
        try {
            return objectMapper.writeValueAsString(coordinates);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("좌표 데이터(GhostData) JSON 변환에 실패했습니다.", e);
        }
    }
}

package MOIYS.project.TimeAttack_Runner_Backend.service;

import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import MOIYS.project.TimeAttack_Runner_Backend.domain.GhostData;
import MOIYS.project.TimeAttack_Runner_Backend.domain.Record;

import MOIYS.project.TimeAttack_Runner_Backend.dto.CoordinateDto;
import MOIYS.project.TimeAttack_Runner_Backend.dto.RecordRequestDto;
import MOIYS.project.TimeAttack_Runner_Backend.dto.RecordResponseDto;

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

    public List<RecordResponseDto> findTopNByRecordTimeAsc(int topN) {
        Pageable pageable = PageRequest.of(0, topN);

        List<Record> records = recordRepository.findByOrderByRecordTimeAsc(pageable);

        return records.stream()
                .map(RecordResponseDto::from)
                .collect(Collectors.toList());
    }

    private String toJsonString(List<CoordinateDto> coordinates) {
        try {
            return objectMapper.writeValueAsString(coordinates);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("좌표 데이터(GhostData) JSON 변환에 실패했습니다.", e);
        }
    }
}

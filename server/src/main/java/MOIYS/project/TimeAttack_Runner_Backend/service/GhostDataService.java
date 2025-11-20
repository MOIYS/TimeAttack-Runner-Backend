package MOIYS.project.TimeAttack_Runner_Backend.service;

import java.util.List;
import java.util.NoSuchElementException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import MOIYS.project.TimeAttack_Runner_Backend.domain.GhostData;

import MOIYS.project.TimeAttack_Runner_Backend.dto.CoordinateDto;
import MOIYS.project.TimeAttack_Runner_Backend.dto.GhostDataResponseDto;

import MOIYS.project.TimeAttack_Runner_Backend.repository.GhostDataRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GhostDataService {
    private final GhostDataRepository ghostDataRepository;
    private final ObjectMapper objectMapper;

    public GhostDataResponseDto findGhostDataByRecordId(Long recordId) {
        GhostData ghostData = ghostDataRepository.findByRecordId(recordId)
                .orElseThrow(() -> new NoSuchElementException("해당 기록(recordId=" + recordId + ")에 대한 고스트 데이터가 없습니다."));

        List<CoordinateDto> coordinates = parseCoordinates(ghostData.getCoordinates());

        return GhostDataResponseDto.from(coordinates);
    }

    private List<CoordinateDto> parseCoordinates(String coordinatesJson) {
        try {
            return objectMapper.readValue(coordinatesJson, new TypeReference<List<CoordinateDto>>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException("고스트 데이터 파싱 중 오류가 발생했습니다.", e);
        }
    }
}

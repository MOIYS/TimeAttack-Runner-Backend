package MOIYS.project.TimeAttack_Runner_Backend.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import MOIYS.project.TimeAttack_Runner_Backend.domain.GhostData;
import MOIYS.project.TimeAttack_Runner_Backend.domain.Record;

import MOIYS.project.TimeAttack_Runner_Backend.dto.CoordinateDto;
import MOIYS.project.TimeAttack_Runner_Backend.dto.GhostDataResponseDto;

import MOIYS.project.TimeAttack_Runner_Backend.repository.RecordRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class GhostDataApiIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RecordRepository recordRepository;

    @Test
    @DisplayName("성공 (200 OK): 고스트 데이터 조회 성공 시 DB 데이터를 정상적으로 가져온다.")
    void return_coordinates_when_record_exists() throws Exception {
        List<CoordinateDto> coordinates = List.of(
                new CoordinateDto(1.0, 1.0, 1.0),
                new CoordinateDto(1.1, 1.0, 1.0)
        );
        GhostDataResponseDto expected = new GhostDataResponseDto(coordinates);

        Record record = new Record("TestUser", 100.0);
        String coordinatesJson = objectMapper.writeValueAsString(coordinates);
        GhostData ghostData = new GhostData(coordinatesJson);

        record.setGhostData(ghostData);
        Record savedRecord = recordRepository.save(record);

        mockMvc.perform(get("/api/ghost/{recordId}", savedRecord.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expected)));
    }
}

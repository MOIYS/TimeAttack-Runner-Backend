package MOIYS.project.TimeAttack_Runner_Backend;

import static org.assertj.core.api.Assertions.assertThat;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import MOIYS.project.TimeAttack_Runner_Backend.domain.GhostData;
import MOIYS.project.TimeAttack_Runner_Backend.domain.Record;

import MOIYS.project.TimeAttack_Runner_Backend.dto.CoordinateDto;
import MOIYS.project.TimeAttack_Runner_Backend.dto.RecordRequestDto;

import MOIYS.project.TimeAttack_Runner_Backend.repository.GhostDataRepository;
import MOIYS.project.TimeAttack_Runner_Backend.repository.RecordRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class RecordApiIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RecordRepository recordRepository;

    @Autowired
    private GhostDataRepository ghostDataRepository;

    @Test
    @DisplayName("BDD 시나리오 검증: 'POST /api/record' 요청 시, 201 응답과 DB 저장을 모두 성공한다.")
    void should_create_record_and_ghost_data_when_valid_request_is_given() throws Exception {
        List<CoordinateDto> coordinates = List.of(new CoordinateDto(1.0, 1.0, 1.0), new CoordinateDto(1.1, 1.0, 1.0));
        RecordRequestDto requestDto = new RecordRequestDto(20, "MOIYS", coordinates);

        mockMvc.perform(post("/api/record")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.recordTime").value(20))
                .andExpect(jsonPath("$.username").value("MOIYS"))
                .andDo(result -> {
                    verifyRecordAndGhostDataSaved(requestDto, coordinates);
                });
    }

    private void verifyRecordAndGhostDataSaved(RecordRequestDto request,
                                               List<CoordinateDto> expectedCoordinates) throws Exception {
        List<Record> records = recordRepository.findAll();
        assertThat(records).hasSize(1);
        Record record = records.get(0);

        List<GhostData> ghostDataList = ghostDataRepository.findAll();
        assertThat(ghostDataList).hasSize(1);
        GhostData ghostData = ghostDataList.get(0);

        assertThat(record.getUsername()).isEqualTo(request.username());
        assertThat(record.getRecordTime()).isEqualTo(request.recordTime());

        assertThat(record.getGhostData()).isEqualTo(ghostData);
        assertThat(ghostData.getRecord()).isEqualTo(record);

        String savedCoordinatesJson = ghostData.getCoordinates();
        List<CoordinateDto> savedCoordinates = objectMapper.readValue(
                savedCoordinatesJson,
                new TypeReference<>() {
                }
        );
        assertThat(savedCoordinates).usingRecursiveComparison()
                .isEqualTo(expectedCoordinates);
    }
}
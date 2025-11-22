package MOIYS.project.TimeAttack_Runner_Backend;

import static org.assertj.core.api.Assertions.assertThat;

import static org.hamcrest.Matchers.hasSize;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    @DisplayName("성공(200 OK): 'POST /api/record' 요청 시, 201 응답과 DB 저장을 모두 성공한다.")
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

    @Test
    @DisplayName("성공(200 OK): 'GET /api/record/leaderboard?top=5 요청 시, 상위 5개 기록만 반환한다.")
    void should_return_top5_leaderboard() throws Exception {
        saveRecords(
                aRecordWith("user6", 68.6),
                aRecordWith("user5", 53.5),
                aRecordWith("user4", 49.4),
                aRecordWith("user3", 33.3),
                aRecordWith("user2", 25.2),
                aRecordWith("user1", 10.1)
        );

        mockMvc.perform(get("/api/record/leaderboard")
                        .param("top", "5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(5)))
                .andExpect(jsonPath("$[0].username").value("user1"))
                .andExpect(jsonPath("$[0].recordTime").value(10.1))
                .andExpect(jsonPath("$[4].username").value("user5"))
                .andExpect(jsonPath("$[4].recordTime").value(53.5));
    }

    @Test
    @DisplayName("엣지(200 OK): DB에 기록이 없을 때, 빈 리더보드를 반환한다.")
    void should_return_empty_list_when_no_records_exist() throws Exception {
        mockMvc.perform(get("/api/record/leaderboard")
                        .param("top", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(0)));
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

    private void saveRecords(Record... records) {
        recordRepository.saveAll(List.of(records));
    }

    private Record aRecordWith(String username, double time) {
        Record record = new Record(username, time);
        record.setGhostData(new GhostData("[]"));
        return record;
    }
}
package MOIYS.project.TimeAttack_Runner_Backend.controller;

import static org.hamcrest.Matchers.hasSize;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

import MOIYS.project.TimeAttack_Runner_Backend.domain.Record;

import MOIYS.project.TimeAttack_Runner_Backend.dto.CoordinateDto;
import MOIYS.project.TimeAttack_Runner_Backend.dto.RecordRequestDto;
import MOIYS.project.TimeAttack_Runner_Backend.dto.RecordResponseDto;

import MOIYS.project.TimeAttack_Runner_Backend.service.RecordService;

@WebMvcTest(RecordController.class)
@Import({RecordControllerTest.MockConfig.class, RecordControllerTest.ValidationConfig.class})
public class RecordControllerTest {
    @Autowired
    private RecordService recordService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @TestConfiguration
    static class MockConfig {
        @Bean
        public RecordService recordService() {
            return Mockito.mock(RecordService.class);
        }
    }

    @TestConfiguration
    static class ValidationConfig {
        @Bean
        public MethodValidationPostProcessor methodValidationPostProcessor() {
            return new MethodValidationPostProcessor();
        }
    }

    @Test
    @DisplayName("성공 (201 Created): 유효한 요청 시, 기록을 생성하고 201을 반환한다.")
    void should_return_201_when_validRequest() throws Exception {
        List<CoordinateDto> coordinates = List.of(new CoordinateDto(1.0, 1.0, 1.0), new CoordinateDto(1.1, 1.0, 1.0));
        RecordRequestDto validRequestDto = new RecordRequestDto(50.2, "MOIYS", coordinates);

        Record dummyRecord = new Record("MOIYS", 50.2);
        ReflectionTestUtils.setField(dummyRecord, "id", 1L);

        given(recordService.createRecord(any(RecordRequestDto.class))).willReturn(dummyRecord);

        mockMvc.perform(post("/api/record")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/record/1"))
                .andExpect(jsonPath("$.username").value("MOIYS"))
                .andExpect(jsonPath("$.recordTime").value(50.2));

        verify(recordService, times(1)).createRecord(any(RecordRequestDto.class));
    }

    @Test
    @DisplayName("실패 (400 Bad Request): 'recordTime'이 음수일 때, 400을 반환한다.")
    void should_return_400_when_time_is_negative() throws Exception {
        RecordRequestDto invalidRequestDto = new RecordRequestDto(-3.4, "MOIYS", List.of());

        mockMvc.perform(post("/api/record")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("실패 (400 Bad Request): 'username'이 비어있을 때 400을 반환한다.")
    void should_return_400_when_username_is_blank() throws Exception {
        RecordRequestDto invalidDto = new RecordRequestDto(20.0, "", List.of());

        mockMvc.perform(post("/api/record")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("실패 (400 Bad Request): 'username'이 100자를 초과할 때 400을 반환한다.")
    void should_return_400_when_username_exceeds_100_chars() throws Exception {
        String longUsername = "a".repeat(101);
        RecordRequestDto invalidDto = new RecordRequestDto(20.0, longUsername, List.of());

        // When & Then
        mockMvc.perform(post("/api/record")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("성공 (200 OK): 리더보드 조회 시, 정렬된 리더보드를 반환한다.")
    void should_return_200_with_leaderboard_when_records_exist() throws Exception {
        int topN = 5;

        List<RecordResponseDto> leaderboard = List.of(
                new RecordResponseDto(2L, "user3", 10.1),
                new RecordResponseDto(1L, "user2", 25.2),
                new RecordResponseDto(0L, "user1", 33.3)
        );

        given(recordService.findTopNByRecordTimeAsc(topN)).willReturn(leaderboard);

        mockMvc.perform(get("/api/record/leaderboard")
                        .param("top", String.valueOf(topN)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[1].username").value("user2"))
                .andExpect(jsonPath("$[1].recordTime").value(25.2));

        verify(recordService, times(1)).findTopNByRecordTimeAsc(topN);
    }

    @Test
    @DisplayName("엣지 (200 OK): DB에 기록이 없을 때, 빈 리더보드를 반환한다.")
    void should_return_200_with_empty_list_when_no_record_exist() throws Exception {
        int topN = 5;

        given(recordService.findTopNByRecordTimeAsc(topN)).willReturn(Collections.emptyList());

        mockMvc.perform(get("/api/record/leaderboard")
                        .param("top", String.valueOf(topN)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("실패 (400 Bad Request): 'top' 파라미터가 1 미만일 때, 400을 반환한다.")
    void should_return_400_when_top_is_invalid() throws Exception {
        int invalidTop = 0;

        mockMvc.perform(get("/api/record/leaderboard")
                        .param("top", String.valueOf(invalidTop)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("올바르지 않은 요청입니다."));

        verify(recordService, times(0)).findTopNByRecordTimeAsc(anyInt());
    }
}
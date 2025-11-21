package MOIYS.project.TimeAttack_Runner_Backend.controller;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import MOIYS.project.TimeAttack_Runner_Backend.dto.RecordRequestDto;

import MOIYS.project.TimeAttack_Runner_Backend.service.RecordService;

@WebMvcTest(RecordController.class)
public class RecordControllerTest {
    @MockitoBean
    private RecordService recordService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("실패 (400 Bad Request): 'recordTime'이 음수일 때, 400을 반환한다.")
    void should_return_400_when_time_is_negative() throws Exception {
        RecordRequestDto invalidRequestDto = new RecordRequestDto(-3.4, "MOIYS", List.of());

        mockMvc.perform(post("/api/record")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequestDto)))
                .andExpect(status().isBadRequest());

        verify(recordService, times(0)).findTopNByRecordTimeAsc(anyInt());
    }

    @Test
    @DisplayName("실패 (400 Bad Request): 'username'이 비어있을 때 400을 반환한다.")
    void should_return_400_when_username_is_blank() throws Exception {
        RecordRequestDto invalidDto = new RecordRequestDto(20.0, "", List.of());

        mockMvc.perform(post("/api/record")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());

        verify(recordService, times(0)).findTopNByRecordTimeAsc(anyInt());
    }

    @Test
    @DisplayName("실패 (400 Bad Request): 'username'이 100자를 초과할 때 400을 반환한다.")
    void should_return_400_when_username_exceeds_100_chars() throws Exception {
        String longUsername = "a".repeat(101);
        RecordRequestDto invalidDto = new RecordRequestDto(20.0, longUsername, List.of());

        mockMvc.perform(post("/api/record")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());

        verify(recordService, times(0)).findTopNByRecordTimeAsc(anyInt());
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
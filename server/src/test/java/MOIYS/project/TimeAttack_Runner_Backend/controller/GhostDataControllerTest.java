package MOIYS.project.TimeAttack_Runner_Backend.controller;

import static org.mockito.BDDMockito.given;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.NoSuchElementException;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import MOIYS.project.TimeAttack_Runner_Backend.dto.CoordinateDto;
import MOIYS.project.TimeAttack_Runner_Backend.dto.GhostDataResponseDto;

import MOIYS.project.TimeAttack_Runner_Backend.service.GhostDataService;

@WebMvcTest(GhostDataController.class)
public class GhostDataControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    private GhostDataService ghostDataService;

    @Test
    @DisplayName("성공 (200 OK): recordId로 조회 시, 좌표 정보를 반환한다.")
    void return_coordinates_when_record_exists() throws Exception {
        Long recordId = 1L;
        List<CoordinateDto> coordinates = List.of(
                new CoordinateDto(1.0, 1.0, 1.0),
                new CoordinateDto(1.1, 1.0, 1.0)
        );
        GhostDataResponseDto expected = new GhostDataResponseDto(coordinates);

        given(ghostDataService.findGhostDataByRecordId(recordId)).willReturn(expected);

        mockMvc.perform(get("/api/ghost/{recordId}", recordId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expected)));
    }

    @Test
    @DisplayName("실패 (404 Not Found): 존재하지 않는 recordId로 조회 시, 404를 반환한다.")
    void return_not_found_when_record_exists() throws Exception {
        Long nonExistentId = 999L;
        given(ghostDataService.findGhostDataByRecordId(nonExistentId))
                .willThrow(new NoSuchElementException());

        mockMvc.perform(get("/api/ghost/{recordId}", nonExistentId))
                .andExpect(status().isNotFound());
    }
}
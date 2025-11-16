package MOIYS.project.TimeAttack_Runner_Backend.service;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import MOIYS.project.TimeAttack_Runner_Backend.domain.Record;

import MOIYS.project.TimeAttack_Runner_Backend.dto.CoordinateDto;
import MOIYS.project.TimeAttack_Runner_Backend.dto.RecordRequestDto;
import MOIYS.project.TimeAttack_Runner_Backend.dto.RecordResponseDto;

import MOIYS.project.TimeAttack_Runner_Backend.repository.RecordRepository;

@ExtendWith(MockitoExtension.class)
public class RecordServiceTest {
    @Mock
    private RecordRepository recordRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private RecordService recordService;

    @Test
    @DisplayName("성공: RecordRequestDto를 받아 Record와 GhostData를 생성하고 저장한다.")
    void should_create_record_with_ghost_data() throws JsonProcessingException {
        List<CoordinateDto> coordinates = List.of(new CoordinateDto(1.0, 1.0, 1.0), new CoordinateDto(1.1, 1.0, 1.0));
        RecordRequestDto requestDto = new RecordRequestDto(50.2, "MOIYS", coordinates);

        String expectedGhostDataJson = "[{\"x\":1.0,\"y\":1.0,\"z\":1.0},{\"x\":1.1,\"y\":1.0,\"z\":1.0}]";
        given(objectMapper.writeValueAsString(coordinates)).willReturn(expectedGhostDataJson);

        given(recordRepository.save(any(Record.class))).willAnswer(invocation -> invocation.getArgument(0));

        Record createdRecord = recordService.createRecord(requestDto);

        ArgumentCaptor<Record> recordCaptor = ArgumentCaptor.forClass(Record.class);
        verify(recordRepository).save(recordCaptor.capture());

        assertThat(recordCaptor.getValue()).satisfies(capturedRecord -> {
            assertThat(capturedRecord.getUsername()).isEqualTo("MOIYS");
            assertThat(capturedRecord.getRecordTime()).isEqualTo(50.2);
            assertThat(capturedRecord.getGhostData()).isNotNull();
            assertThat(capturedRecord.getGhostData().getCoordinates()).isEqualTo(expectedGhostDataJson);
            assertThat(capturedRecord.getGhostData().getRecord()).as("양방향 연관관계 검증").isEqualTo(capturedRecord);
        });
    }

    @Test
    @DisplayName("성공: 상위 N개의 기록을 RecordResponseDto 리스트로 변환하여, 반환한다.")
    void should_return_topN_when_record_exist() {
        int topN = 5;
        Pageable pageable = PageRequest.of(0, topN);

        Record record1 = new Record("user1", 10.1);
        Record record2 = new Record("user2", 25.2);
        Record record3 = new Record("user3", 33.3);
        Record record4 = new Record("user4", 49.4);
        Record record5 = new Record("user5", 53.5);
        List<Record> records = List.of(record1, record2, record3, record4, record5);

        given(recordRepository.findByOrderByRecordTimeAsc(pageable))
                .willReturn(records);

        List<RecordResponseDto> leaderboard = recordService.findTopNByRecordTimeAsc(topN);

        List<RecordResponseDto> expected = records.stream()
                .map(record -> new RecordResponseDto(null, record.getUsername(), record.getRecordTime()))
                .toList();

        assertThat(leaderboard)
                .hasSize(5)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("엣지: DB에 기록이 없을 때, 빈 리더보드(List)를 반환한다.")
    void should_return_empty_list_when_no_records_exist() {
        int topN = 5;

        Pageable pageable = PageRequest.of(0, topN);

        given(recordRepository.findByOrderByRecordTimeAsc(pageable))
                .willReturn(List.of());

        List<RecordResponseDto> leaderboard = recordService.findTopNByRecordTimeAsc(topN);

        assertThat(leaderboard).isEmpty();
    }
}
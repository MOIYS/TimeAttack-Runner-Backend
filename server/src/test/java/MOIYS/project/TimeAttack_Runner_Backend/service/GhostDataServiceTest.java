package MOIYS.project.TimeAttack_Runner_Backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import static org.mockito.BDDMockito.given;

import java.util.NoSuchElementException;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import MOIYS.project.TimeAttack_Runner_Backend.domain.GhostData;

import MOIYS.project.TimeAttack_Runner_Backend.dto.CoordinateDto;
import MOIYS.project.TimeAttack_Runner_Backend.dto.GhostDataResponseDto;

import MOIYS.project.TimeAttack_Runner_Backend.repository.GhostDataRepository;

@ExtendWith(MockitoExtension.class)
public class GhostDataServiceTest {
    @Mock
    private GhostDataRepository ghostDataRepository;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private GhostDataService ghostService;

    @Test
    @DisplayName("성공: recordId로 고스트 데이터를 조회하면, 좌표 정보를 반환한다.")
    void should_return_dto_when_ghost_data_exists() throws JsonProcessingException {
        Long recordId = 1L;
        String coordinatesJson = "[{\"x\":1.0,\"y\":1.0,\"z\":1.0},{\"x\":1.1,\"y\":1.0,\"z\":1.0}]";
        GhostData ghostData = new GhostData(coordinatesJson);

        given(ghostDataRepository.findByRecordId(recordId)).willReturn(Optional.of(ghostData));

        GhostDataResponseDto result = ghostService.findGhostDataByRecordId(recordId);

        assertThat(result.ghostData())
                .containsExactly(
                        new CoordinateDto(1.0, 1.0, 1.0),
                        new CoordinateDto(1.1, 1.0, 1.0)
                );
    }

    @Test
    @DisplayName("실패: 존재하지 않는 recordId로 조회 시, 조회 실패 예외가 발생한다.")
    void should_throw_exception_when_record_does_not_exist() {
        Long unknownRecordId = 999L;
        given(ghostDataRepository.findByRecordId(unknownRecordId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> ghostService.findGhostDataByRecordId(unknownRecordId))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("해당 기록(recordId=" + unknownRecordId + ")에 대한 고스트 데이터가 없습니다.");
    }
}
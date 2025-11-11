package MOIYS.project.TimeAttack_Runner_Backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record RecordRequestDto(
        @Min(value = 0, message = "기록 시간은 0보다 작을 수 없습니다.")
        double recordTime,

        @NotBlank(message = "사용자 이름은 비어있을 수 없습니다.")
        @Size(max = 100, message = "사용자 이름은 100자를 초과할 수 없습니다.")
        String username,

        List<CoordinateDto> ghostData
) {
}

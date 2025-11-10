package MOIYS.project.TimeAttack_Runner_Backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record RecordRequestDto(
        @Min(value = 0, message = "time은 0보다 작을 수 없습니다.")
        double time,

        @NotBlank(message = "username은 비어있을 수 없습니다.")
        String username,

        List<CoordinateDto> ghostData
) {
}

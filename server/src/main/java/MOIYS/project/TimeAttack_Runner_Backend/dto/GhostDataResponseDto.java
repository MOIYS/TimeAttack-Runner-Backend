package MOIYS.project.TimeAttack_Runner_Backend.dto;

import java.util.List;

public record GhostDataResponseDto(
        List<CoordinateDto> ghostData
) {
    public static GhostDataResponseDto from(List<CoordinateDto> coordinates) {
        return new GhostDataResponseDto(coordinates);
    }
}

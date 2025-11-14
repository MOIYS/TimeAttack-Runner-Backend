package MOIYS.project.TimeAttack_Runner_Backend.dto;

import java.util.Objects;

import MOIYS.project.TimeAttack_Runner_Backend.domain.Record;

public record RecordResponseDto(
        Long id,
        String username,
        Double recordTime
) {
    public static RecordResponseDto from(Record entity) {
        Objects.requireNonNull(entity, "Record Entity는 null이 아니다.");
        return new RecordResponseDto(
                entity.getId(),
                entity.getUsername(),
                entity.getRecordTime()
        );
    }
}
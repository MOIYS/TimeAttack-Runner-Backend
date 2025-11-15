package MOIYS.project.TimeAttack_Runner_Backend.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RecordRequestDtoTest {

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void tearDown() {
        factory.close();
    }

    @Test
    @DisplayName("실패: 'time'이 음수일 때, 유효성 검사에 실패한다.")
    void should_fail_when_time_is_negative() {
        var invalidRequest = new RecordRequestDto(-10.0, "MOIYS", List.of());

        Set<ConstraintViolation<RecordRequestDto>> violations = validator.validate(invalidRequest);

        assertThat(violations).isNotEmpty();
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("time");
    }

    @Test
    @DisplayName("실패: 'username'이 비어있을 때, 유효성 검사에 실패한다.")
    void should_fail_when_username_is_blank() {
        var invalidRequest = new RecordRequestDto(20.0, "", List.of());

        Set<ConstraintViolation<RecordRequestDto>> violations = validator.validate(invalidRequest);

        assertThat(violations).isNotEmpty();
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("username");
    }

    @Test
    @DisplayName("실패: username이 100자를 초과할 때, 유효성 검사에 실패한다.")
    void should_fail_when_username_exceeds_100_characters() {
        String longUsername = "a".repeat(101);
        var invalidRequest = new RecordRequestDto(20.0, longUsername, List.of());

        Set<ConstraintViolation<RecordRequestDto>> violations = validator.validate(invalidRequest);

        assertThat(violations).isNotEmpty();
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("username");
    }
}
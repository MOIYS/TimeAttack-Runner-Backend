package MOIYS.project.TimeAttack_Runner_Backend.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

import MOIYS.project.TimeAttack_Runner_Backend.domain.Record;

@DataJpaTest
@ActiveProfiles("test")
public class RecordRepositoryTest {
    @Autowired
    private RecordRepository recordRepository;

    @Test
    @DisplayName("성공: Record Entity를 저장하고, recordId로 다시 조회할 수 있다.")
    void should_save_record_and_retrieve_by_id() {
        Record newRecord = new Record("MOIYS", 50.2);

        Record savedRecord = recordRepository.save(newRecord);

        assertThat(savedRecord.getId()).isNotNull();

        Record foundRecord = recordRepository.findById(savedRecord.getId()).orElseThrow();

        assertThat(foundRecord.getUsername()).isEqualTo("MOIYS");
        assertThat(foundRecord.getRecordTime()).isEqualTo(50.2);
    }
}
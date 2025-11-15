package MOIYS.project.TimeAttack_Runner_Backend.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

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

    @Test
    @DisplayName("성공: recordTime 기준 오름차순으로 상위 5개 기록을 조회한다.")
    void should_find_top5_by_record_time_asc() {
        recordRepository.save(new Record("user1", 10.1));
        recordRepository.save(new Record("user2", 25.2));
        recordRepository.save(new Record("user3", 33.3));
        recordRepository.save(new Record("user4", 49.4));
        recordRepository.save(new Record("user5", 53.5));
        recordRepository.save(new Record("user6", 68.6));

        Pageable topFive = PageRequest.of(0, 5);

        List<Record> leaderboard = recordRepository.findByOrderByRecordTimeAsc(topFive);

        assertThat(leaderboard).hasSize(5);

        assertThat(leaderboard.get(0).getRecordTime()).isEqualTo(10.1);
        assertThat(leaderboard.get(3).getRecordTime()).isEqualTo(49.4);
    }
}
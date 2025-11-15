package MOIYS.project.TimeAttack_Runner_Backend.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import MOIYS.project.TimeAttack_Runner_Backend.domain.GhostData;
import MOIYS.project.TimeAttack_Runner_Backend.domain.Record;

@DataJpaTest
@ActiveProfiles("test")
public class GhostDataRepositoryTest {
    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private GhostDataRepository ghostDataRepository;

    @Test
    @DisplayName("성공: GhostData Entity를 저장하고, 연관된 Record와 함께 조회할 수 있다.")
    void should_save_and_retrieve_ghost_data_with_linked_record() {
        Record newRecord = new Record("MOIYS", 50.2);
        Record savedRecord = testEntityManager.persistAndFlush(newRecord);

        String testCoordinates = "[{\"x\":1.0,\"y\":1.0,\"z\":1.0},{\"x\":1.1,\"y\":1.0,\"z\":1.0}]";
        GhostData newGhostData = new GhostData(testCoordinates);
        savedRecord.setGhostData(newGhostData);

        GhostData savedGhostData = ghostDataRepository.save(newGhostData);
        testEntityManager.clear();

        Optional<GhostData> foundGhostData = ghostDataRepository.findById(savedGhostData.getId());

        assertThat(foundGhostData)
                .isPresent()
                .get()
                .satisfies(ghostData -> {
                    assertThat(ghostData.getId()).isEqualTo(savedGhostData.getId());
                    assertThat(ghostData.getCoordinates()).isEqualTo(testCoordinates);
                    assertThat(ghostData.getRecord()).isNotNull();
                    assertThat(ghostData.getRecord().getId()).isEqualTo(savedRecord.getId());
                    assertThat(ghostData.getRecord().getUsername()).isEqualTo("MOIYS");
                    assertThat(ghostData.getRecord().getRecordTime()).isEqualTo(50.2);
                });
    }
}
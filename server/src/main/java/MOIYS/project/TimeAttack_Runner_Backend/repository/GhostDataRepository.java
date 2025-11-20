package MOIYS.project.TimeAttack_Runner_Backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import MOIYS.project.TimeAttack_Runner_Backend.domain.GhostData;

public interface GhostDataRepository extends JpaRepository<GhostData, Long> {
    
    Optional<GhostData> findByRecordId(Long recordId);
}
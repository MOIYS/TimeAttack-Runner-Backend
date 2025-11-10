package MOIYS.project.TimeAttack_Runner_Backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import MOIYS.project.TimeAttack_Runner_Backend.domain.GhostData;

public interface GhostDataRepository extends JpaRepository<GhostData, Long> {
}
package MOIYS.project.TimeAttack_Runner_Backend.domain;

import java.time.LocalDateTime;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "record")
public class Record {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String username;

    @Column(nullable = false)
    private Double recordTime;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToOne(mappedBy = "record", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private GhostData ghostData;

    public Record(String username, Double recordTime) {
        this.username = username;
        this.recordTime = recordTime;
    }

    public void setGhostData(GhostData ghostData) {
        this.ghostData = ghostData;

        if (ghostData != null) {
            ghostData.setRecord(this);
        }
    }
}
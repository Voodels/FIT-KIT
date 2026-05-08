package app.fitkit.api.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "workout_logs")
@Getter
@Setter
@NoArgsConstructor
public class WorkoutLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    // --- THE RELATIONSHIP ---
    // Many Workouts belong to One User. 
    // FetchType.LAZY means we don't load the whole User object unless we specifically ask for it (saves memory).
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // We will store the AWS S3 URL here
    @Column(length = 1000)
    private String photoUrl;

    @Column(length = 500)
    private String caption;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<String> musclesTargeted;

@OneToMany(mappedBy = "workoutLog", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WorkoutSet> sets = new ArrayList<>();

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime loggedAt;
}
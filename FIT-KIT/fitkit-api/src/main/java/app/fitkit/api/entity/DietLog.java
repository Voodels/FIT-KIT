package app.fitkit.api.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import app.fitkit.api.dto.ParsedItem;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
// THE SDE UPGRADE: We tell PostgreSQL that a User can only have ONE card per specific date.
@Table(name = "daily_diet_logs", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "logged_date"})
})
@Getter
@Setter
@NoArgsConstructor
public class DietLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // The "Card Note": A larger text block where they can just brain-dump their day
    @Column(length = 2000)
    private String journalNote; 

    // The estimated totals for the day
    @Column(nullable = false)
    private int totalCalories;

    @Column(nullable = false)
    private double totalProtein;

    @Column(nullable = false)
    private double totalCarbs;

    @Column(nullable = false)
    private double totalFats;

    @Column(name = "logged_date", nullable = false)
    private LocalDate loggedDate;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<ParsedItem> extractedItems;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
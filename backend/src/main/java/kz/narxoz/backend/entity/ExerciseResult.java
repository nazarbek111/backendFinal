package kz.narxoz.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "exercise_results")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ExerciseResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_id")
    @JsonIgnoreProperties({"results", "parent", "handler", "hibernateLazyInitializer"}) // Разорвать связь с ребенком
    private Child child;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_id")
    @JsonIgnoreProperties({"results", "handler", "hibernateLazyInitializer"}) // Разорвать связь с упражнением
    private Exercise exercise;

    private Boolean correct;
    private Integer timeTaken; // в секундах

    @CreationTimestamp
    private LocalDateTime submittedAt;
}
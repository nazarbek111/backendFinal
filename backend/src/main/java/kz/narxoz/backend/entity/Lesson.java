package kz.narxoz.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import kz.narxoz.backend.entity.enums.LessonType;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import kz.narxoz.backend.entity.enums.Role;
@Entity
@Table(name = "lessons")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lesson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "unit_id")
    private Unit unit;

    private String title;

    @Enumerated(EnumType.STRING)
    private LessonType type; // PHONICS, HANDWRITING, SIGHT_WORDS, VOCABULARY

    private Integer orderIndex;
    private Boolean published = false;
    private Integer xpReward = 10;
}
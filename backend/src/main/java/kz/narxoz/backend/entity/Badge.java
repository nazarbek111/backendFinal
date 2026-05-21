package kz.narxoz.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "badges")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Badge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;        // "First Lesson"
    private String description; // "Завершил первый урок"
    private String icon;        // emoji или url
}
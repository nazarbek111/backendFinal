package kz.narxoz.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import kz.narxoz.backend.entity.enums.Role;

@Entity
@Table(name = "children")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Child {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "parent_id", nullable = false)
    private Parent parent;

    private String name;
    private Integer age;
    private String avatar;

    private Integer xpPoints = 0;
    private Integer level = 1;
    private Integer streak = 0;
    private LocalDate lastActiveDate;

    @Enumerated(EnumType.STRING)
    private Role role = Role.CHILD;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "child")
    @JsonIgnore
    private List<ExerciseResult> results;
}
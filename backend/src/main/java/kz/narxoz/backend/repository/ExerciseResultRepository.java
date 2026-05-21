package kz.narxoz.backend.repository;

import kz.narxoz.backend.entity.ExerciseResult;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ExerciseResultRepository extends JpaRepository<ExerciseResult, Long> {
    List<ExerciseResult> findAllByChildId(Long childId);
    List<ExerciseResult> findAllByChildIdAndExerciseId(Long childId, Long exerciseId);
    List<ExerciseResult> findAllByChildIdOrderBySubmittedAtDesc(Long childId);
}
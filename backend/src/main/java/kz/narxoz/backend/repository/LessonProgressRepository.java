package kz.narxoz.backend.repository;

import kz.narxoz.backend.entity.LessonProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LessonProgressRepository extends JpaRepository<LessonProgress, Long> {
    List<LessonProgress> findAllByChildId(Long childId);
    Optional<LessonProgress> findByChildIdAndLessonId(Long childId, Long lessonId);
    long countByChildIdAndCompleted(Long childId, Boolean completed);
    long countByCompleted(Boolean completed);
}

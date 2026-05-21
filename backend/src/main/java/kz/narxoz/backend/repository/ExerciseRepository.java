package kz.narxoz.backend.repository;

import kz.narxoz.backend.entity.Exercise;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
    Page<Exercise> findByLessonId(Long lessonId, Pageable pageable);
}
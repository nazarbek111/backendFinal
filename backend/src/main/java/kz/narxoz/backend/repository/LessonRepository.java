package kz.narxoz.backend.repository;

import kz.narxoz.backend.entity.Lesson;
import kz.narxoz.backend.entity.enums.LessonType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LessonRepository extends JpaRepository<Lesson, Long> {

    @Query("SELECT l FROM Lesson l WHERE " +
            "(:unitId IS NULL OR l.unit.id = :unitId) AND " +
            "(:type IS NULL OR l.type = :type) AND " +
            "(:published IS NULL OR l.published = :published)")
    Page<Lesson> findWithFilters(
            @Param("unitId") Long unitId,
            @Param("type") LessonType type,
            @Param("published") Boolean published,
            Pageable pageable);
}

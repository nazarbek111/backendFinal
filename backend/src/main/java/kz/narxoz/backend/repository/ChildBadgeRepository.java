package kz.narxoz.backend.repository;

import kz.narxoz.backend.entity.ChildBadge;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ChildBadgeRepository extends JpaRepository<ChildBadge, Long> {
    List<ChildBadge> findAllByChildId(Long childId);
    boolean existsByChildIdAndBadgeId(Long childId, Long badgeId);
}
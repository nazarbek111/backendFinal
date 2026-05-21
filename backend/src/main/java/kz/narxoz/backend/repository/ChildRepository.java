package kz.narxoz.backend.repository;

import kz.narxoz.backend.entity.Child;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ChildRepository extends JpaRepository<Child, Long> {

    List<Child> findAllByParentId(Long parentId);

    long countByLastActiveDateGreaterThanEqual(LocalDate date);

    @Query("SELECT c FROM Child c WHERE c.age BETWEEN :minAge AND :maxAge " +
            "ORDER BY c.xpPoints DESC")
    List<Child> findLeaderboard(
            @Param("minAge") int minAge,
            @Param("maxAge") int maxAge,
            org.springframework.data.domain.Pageable pageable);

    @Query("SELECT c FROM Child c WHERE c.lastActiveDate < :yesterday OR c.lastActiveDate IS NULL")
    List<Child> findChildrenInactiveToday(@Param("yesterday") LocalDate yesterday);
}
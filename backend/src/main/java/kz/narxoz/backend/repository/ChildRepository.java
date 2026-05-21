package kz.narxoz.backend.repository;

import kz.narxoz.backend.entity.Child;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ChildRepository extends JpaRepository<Child, Long> {
    List<Child> findAllByParentId(Long parentId);
    long countByLastActiveDateGreaterThanEqual(LocalDate date);
}

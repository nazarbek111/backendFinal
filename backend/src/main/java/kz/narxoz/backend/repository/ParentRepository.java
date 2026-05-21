package kz.narxoz.backend.repository;

import kz.narxoz.backend.entity.Parent;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ParentRepository extends JpaRepository<Parent, Long> {
    Optional<Parent> findByEmail(String email);
    boolean existsByEmail(String email);
    void deleteByEmail(String email);
}
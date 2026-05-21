package kz.narxoz.backend.repository;

import kz.narxoz.backend.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findAllByParentIdOrderByCreatedAtDesc(Long parentId);
}
package kz.narxoz.backend.service;

import kz.narxoz.backend.entity.Notification;
import kz.narxoz.backend.entity.Parent;
import kz.narxoz.backend.entity.enums.NotificationType;
import kz.narxoz.backend.repository.NotificationRepository;
import kz.narxoz.backend.repository.ParentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final ParentRepository parentRepository;

    public List<Notification> getNotificationsForParent(String email) {
        Parent parent = parentRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Parent not found"));
        return notificationRepository.findAllByParentIdOrderByCreatedAtDesc(parent.getId());
    }

    public Notification markAsRead(Long id, String email) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        Parent parent = parentRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Parent not found"));

        // Ownership check — parent can only mark their own notifications
        if (!notification.getParentId().equals(parent.getId())) {
            throw new RuntimeException("Access denied");
        }

        notification.setIsRead(true);
        return notificationRepository.save(notification);
    }

    public Notification createProgressNotification(Long parentId, String childName, String lessonName) {
        return createNotification(parentId,
                "Your child " + childName + " completed lesson: " + lessonName + "!",
                NotificationType.PROGRESS);
    }

    public Notification createAchievementNotification(Long parentId, String childName, String badgeName) {
        return createNotification(parentId,
                "Your child " + childName + " earned badge: " + badgeName + "!",
                NotificationType.ACHIEVEMENT);
    }

    public Notification createStreakRiskNotification(Long parentId, String childName) {
        return createNotification(parentId,
                "Streak at risk: " + childName + " hasn't studied today.",
                NotificationType.STREAK_RISK);
    }

    private Notification createNotification(Long parentId, String message, NotificationType type) {
        Notification notification = Notification.builder()
                .parentId(parentId)
                .message(message)
                .type(type)
                .isRead(false)
                .build();
        return notificationRepository.save(notification);
    }
}

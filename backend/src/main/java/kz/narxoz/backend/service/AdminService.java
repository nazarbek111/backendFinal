package kz.narxoz.backend.service;

import kz.narxoz.backend.entity.ActivityLog;
import kz.narxoz.backend.repository.ActivityLogRepository;
import kz.narxoz.backend.repository.ChildRepository;
import kz.narxoz.backend.repository.LessonProgressRepository;
import kz.narxoz.backend.repository.ParentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final ActivityLogRepository activityLogRepository;
    private final ParentRepository parentRepository;
    private final ChildRepository childRepository;
    private final LessonProgressRepository lessonProgressRepository;

    public Page<ActivityLog> getLogs(Pageable pageable) {
        return activityLogRepository.findAllByOrderByCreatedAtDesc(pageable);
    }

    public Map<String, Long> getStats() {
        Map<String, Long> stats = new LinkedHashMap<>();

        stats.put("totalParents", parentRepository.count());
        stats.put("totalChildren", childRepository.count());
        stats.put("completedLessons", lessonProgressRepository.countByCompleted(true));
        stats.put("activeChildrenToday",
                childRepository.countByLastActiveDateGreaterThanEqual(LocalDate.now()));

        return stats;
    }

    @org.springframework.transaction.annotation.Transactional
    public void logAction(String adminEmail, String action) {
        ActivityLog log = ActivityLog.builder()
                .adminEmail(adminEmail)
                .action(action)
                .build();
        activityLogRepository.save(log);
    }
}

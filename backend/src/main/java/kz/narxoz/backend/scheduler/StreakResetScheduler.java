package kz.narxoz.backend.scheduler;

import kz.narxoz.backend.entity.Child;
import kz.narxoz.backend.repository.ChildRepository;
import kz.narxoz.backend.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;


@Component
@RequiredArgsConstructor
@Slf4j
public class StreakResetScheduler {

    private final ChildRepository childRepository;
    private final NotificationService notificationService;

    @Scheduled(cron = "0 5 0 * * *")
    @Transactional
    public void resetInactiveStreaks() {
        LocalDate yesterday = LocalDate.now().minusDays(1);

        List<Child> inactiveChildren = childRepository.findChildrenInactiveToday(yesterday);

        int reset = 0;
        for (Child child : inactiveChildren) {
            if (child.getStreak() > 0) {
                notificationService.createStreakRiskNotification(
                        child.getParent().getId(), child.getName());

                child.setStreak(0);
                childRepository.save(child);
                reset++;
            }
        }

        log.info("Streak reset job completed: {} children reset", reset);
    }
}
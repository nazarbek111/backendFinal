package kz.narxoz.backend.service;

import kz.narxoz.backend.entity.*;
import kz.narxoz.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GamificationService {

    private final ChildRepository childRepository;
    private final BadgeRepository badgeRepository;
    private final ChildBadgeRepository childBadgeRepository;
    private final LessonProgressRepository lessonProgressRepository;

    // ===== XP & Levels =====
    public void addXp(Child child, int xp) {
        child.setXpPoints(child.getXpPoints() + xp);
        child.setLevel(calculateLevel(child.getXpPoints()));
        childRepository.save(child);
    }

    public int calculateLevel(int xp) {
        if (xp >= 500) return 4;
        if (xp >= 250) return 3;
        if (xp >= 100) return 2;
        return 1;
    }

    // ===== Streak =====
    public void updateStreak(Child child) {
        LocalDate today = LocalDate.now();
        LocalDate lastActive = child.getLastActiveDate();

        if (lastActive == null) {
            child.setStreak(1);
        } else if (lastActive.equals(today.minusDays(1))) {
            child.setStreak(child.getStreak() + 1);
        } else if (lastActive.equals(today)) {
            return; // already counted today
        } else {
            child.setStreak(1); // streak broken
        }

        child.setLastActiveDate(today);
        childRepository.save(child);
    }

    // ===== Badges — returns list of newly awarded badge names =====
    public List<String> checkAndAwardBadges(Child child) {
        List<String> newBadges = new ArrayList<>();

        long completedLessons = lessonProgressRepository
                .countByChildIdAndCompleted(child.getId(), true);

        if (completedLessons >= 1) {
            if (awardBadge(child, "First Lesson", "Completed first lesson!", "🎯"))
                newBadges.add("First Lesson");
        }
        if (child.getStreak() >= 7) {
            if (awardBadge(child, "7-Day Streak", "7 days in a row!", "🔥"))
                newBadges.add("7-Day Streak");
        }
        if (child.getXpPoints() >= 100) {
            if (awardBadge(child, "100 XP", "Earned 100 XP!", "⭐"))
                newBadges.add("100 XP");
        }
        if (child.getLevel() >= 2) {
            if (awardBadge(child, "Level Up", "Reached level 2!", "🚀"))
                newBadges.add("Level Up");
        }

        return newBadges;
    }

    /**
     * Returns true if the badge was newly awarded, false if already existed.
     */
    private boolean awardBadge(Child child, String badgeName, String description, String icon) {
        Badge badge = badgeRepository.findByName(badgeName)
                .orElseGet(() -> badgeRepository.save(
                        Badge.builder().name(badgeName).description(description).icon(icon).build()));

        if (!childBadgeRepository.existsByChildIdAndBadgeId(child.getId(), badge.getId())) {
            childBadgeRepository.save(ChildBadge.builder().child(child).badge(badge).build());
            return true;
        }
        return false;
    }
}

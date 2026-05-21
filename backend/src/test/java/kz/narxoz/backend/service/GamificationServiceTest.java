package kz.narxoz.backend.service;

import kz.narxoz.backend.entity.*;
import kz.narxoz.backend.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GamificationServiceTest {

    @Mock private ChildRepository childRepository;
    @Mock private BadgeRepository badgeRepository;
    @Mock private ChildBadgeRepository childBadgeRepository;
    @Mock private LessonProgressRepository lessonProgressRepository;

    @InjectMocks
    private GamificationService gamificationService;

    private Child child;

    @BeforeEach
    void setUp() {
        child = Child.builder()
                .id(1L)
                .name("Alice")
                .age(5)
                .xpPoints(0)
                .level(1)
                .streak(0)
                .build();
    }

    // ===== XP & Level tests =====

    @Test
    void addXp_increasesXpAndSavesChild() {
        when(childRepository.save(any())).thenReturn(child);

        gamificationService.addXp(child, 30);

        assertThat(child.getXpPoints()).isEqualTo(30);
        verify(childRepository).save(child);
    }

    @Test
    void calculateLevel_returnsLevel1_when0xp() {
        assertThat(gamificationService.calculateLevel(0)).isEqualTo(1);
    }

    @Test
    void calculateLevel_returnsLevel2_when100xp() {
        assertThat(gamificationService.calculateLevel(100)).isEqualTo(2);
    }

    @Test
    void calculateLevel_returnsLevel3_when250xp() {
        assertThat(gamificationService.calculateLevel(250)).isEqualTo(3);
    }

    @Test
    void calculateLevel_returnsLevel4_when500xp() {
        assertThat(gamificationService.calculateLevel(500)).isEqualTo(4);
    }

    @Test
    void addXp_triggersLevelUpAt100() {
        child.setXpPoints(90);
        when(childRepository.save(any())).thenReturn(child);

        gamificationService.addXp(child, 10);

        assertThat(child.getXpPoints()).isEqualTo(100);
        assertThat(child.getLevel()).isEqualTo(2);
    }

    // ===== Streak tests =====

    @Test
    void updateStreak_setsStreak1_onFirstUse() {
        child.setLastActiveDate(null);
        when(childRepository.save(any())).thenReturn(child);

        gamificationService.updateStreak(child);

        assertThat(child.getStreak()).isEqualTo(1);
        assertThat(child.getLastActiveDate()).isEqualTo(LocalDate.now());
    }

    @Test
    void updateStreak_incrementsStreak_whenLastActiveDateWasYesterday() {
        child.setStreak(3);
        child.setLastActiveDate(LocalDate.now().minusDays(1));
        when(childRepository.save(any())).thenReturn(child);

        gamificationService.updateStreak(child);

        assertThat(child.getStreak()).isEqualTo(4);
    }

    @Test
    void updateStreak_resetsStreak_whenStreakBroken() {
        child.setStreak(5);
        child.setLastActiveDate(LocalDate.now().minusDays(3));
        when(childRepository.save(any())).thenReturn(child);

        gamificationService.updateStreak(child);

        assertThat(child.getStreak()).isEqualTo(1);
    }

    @Test
    void updateStreak_doesNothing_whenAlreadyActivatedToday() {
        child.setStreak(5);
        child.setLastActiveDate(LocalDate.now());

        gamificationService.updateStreak(child);

        assertThat(child.getStreak()).isEqualTo(5); // unchanged
        verify(childRepository, never()).save(any());
    }

    // ===== Badge tests =====

    @Test
    void checkAndAwardBadges_awardsFirstLessonBadge() {
        child.setXpPoints(10);
        when(lessonProgressRepository.countByChildIdAndCompleted(1L, true)).thenReturn(1L);
        when(badgeRepository.findByName("First Lesson")).thenReturn(Optional.of(
                Badge.builder().id(1L).name("First Lesson").build()));
        when(childBadgeRepository.existsByChildIdAndBadgeId(1L, 1L)).thenReturn(false);
        when(childBadgeRepository.save(any())).thenReturn(new ChildBadge());

        List<String> awarded = gamificationService.checkAndAwardBadges(child);

        assertThat(awarded).contains("First Lesson");
    }

    @Test
    void checkAndAwardBadges_doesNotDuplicateBadge() {
        child.setXpPoints(150);
        when(lessonProgressRepository.countByChildIdAndCompleted(anyLong(), eq(true))).thenReturn(2L);
        Badge badge = Badge.builder().id(1L).name("First Lesson").build();
        when(badgeRepository.findByName(anyString())).thenReturn(Optional.of(badge));
        // badge already awarded
        when(childBadgeRepository.existsByChildIdAndBadgeId(anyLong(), anyLong())).thenReturn(true);

        List<String> awarded = gamificationService.checkAndAwardBadges(child);

        assertThat(awarded).doesNotContain("First Lesson");
        verify(childBadgeRepository, never()).save(any());
    }

    @Test
    void checkAndAwardBadges_awards100XpBadge() {
        child.setXpPoints(100);
        when(lessonProgressRepository.countByChildIdAndCompleted(1L, true)).thenReturn(0L);
        Badge badge = Badge.builder().id(2L).name("100 XP").build();
        when(badgeRepository.findByName("100 XP")).thenReturn(Optional.of(badge));
        when(childBadgeRepository.existsByChildIdAndBadgeId(1L, 2L)).thenReturn(false);
        when(childBadgeRepository.save(any())).thenReturn(new ChildBadge());

        List<String> awarded = gamificationService.checkAndAwardBadges(child);

        assertThat(awarded).contains("100 XP");
    }

    // helper for Mockito static import
    private static <T> T eq(T value) {
        return org.mockito.ArgumentMatchers.eq(value);
    }
}

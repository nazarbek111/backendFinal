package kz.narxoz.backend.service;

import kz.narxoz.backend.dto.request.ExerciseSubmitRequest;
import kz.narxoz.backend.dto.response.ChildBadgeResponse;
import kz.narxoz.backend.dto.response.LessonProgressResponse;
import kz.narxoz.backend.entity.*;
import kz.narxoz.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProgressService {

    private final ChildRepository childRepository;
    private final LessonRepository lessonRepository;
    private final ExerciseRepository exerciseRepository;
    private final LessonProgressRepository lessonProgressRepository;
    private final ExerciseResultRepository exerciseResultRepository;
    private final ChildBadgeRepository childBadgeRepository;
    private final GamificationService gamificationService;
    private final NotificationService notificationService;

    @Transactional
    public String submitExercise(Long exerciseId, ExerciseSubmitRequest request, String parentEmail) {
        Child child = childRepository.findById(request.getChildId())
                .orElseThrow(() -> new RuntimeException("Child not found"));

        if (!child.getParent().getEmail().equals(parentEmail)) {
            throw new RuntimeException("Access denied");
        }

        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new RuntimeException("Exercise not found"));

        boolean isCorrect = exercise.getCorrectAnswer()
                .trim()
                .equalsIgnoreCase(request.getAnswer().trim());

        ExerciseResult result = ExerciseResult.builder()
                .child(child)
                .exercise(exercise)
                .correct(isCorrect)
                .timeTaken(request.getTimeTaken())
                .build();

        exerciseResultRepository.save(result);
        return isCorrect ? "Correct!" : "Incorrect. Try again!";
    }

    @Transactional
    public LessonProgressResponse completeLesson(Long lessonId, Long childId, String parentEmail) {
        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new RuntimeException("Child not found"));

        if (!child.getParent().getEmail().equals(parentEmail)) {
            throw new RuntimeException("Access denied");
        }

        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Lesson not found"));

        boolean alreadyCompleted = lessonProgressRepository
                .findByChildIdAndLessonId(childId, lessonId)
                .map(LessonProgress::getCompleted)
                .orElse(false);

        if (alreadyCompleted) {
            throw new RuntimeException("Lesson already completed");
        }

        LessonProgress progress = LessonProgress.builder()
                .child(child)
                .lesson(lesson)
                .completed(true)
                .xpEarned(lesson.getXpReward())
                .build();

        lessonProgressRepository.save(progress);

        gamificationService.addXp(child, lesson.getXpReward());
        gamificationService.updateStreak(child);

        List<String> newBadges = gamificationService.checkAndAwardBadges(child);

        notificationService.createProgressNotification(
                child.getParent().getId(), child.getName(), lesson.getTitle());

        for (String badgeName : newBadges) {
            notificationService.createAchievementNotification(
                    child.getParent().getId(), child.getName(), badgeName);
        }

        return mapToResponse(progress);
    }

    public List<LessonProgressResponse> getProgress(Long childId, String parentEmail) {
        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new RuntimeException("Child not found"));
        if (!child.getParent().getEmail().equals(parentEmail)) {
            throw new RuntimeException("Access denied");
        }
        return lessonProgressRepository.findAllByChildId(childId)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public List<ChildBadgeResponse> getBadges(Long childId, String parentEmail) {
        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new RuntimeException("Child not found"));
        if (!child.getParent().getEmail().equals(parentEmail)) {
            throw new RuntimeException("Access denied");
        }
        return childBadgeRepository.findAllByChildId(childId)
                .stream()
                .map(cb -> new ChildBadgeResponse(
                        cb.getBadge().getName(),
                        cb.getBadge().getDescription(),
                        cb.getBadge().getIcon(),
                        cb.getEarnedAt()))
                .collect(Collectors.toList());
    }

    private LessonProgressResponse mapToResponse(LessonProgress progress) {
        LessonProgressResponse r = new LessonProgressResponse();
        r.setId(progress.getId());
        r.setLessonId(progress.getLesson().getId());
        r.setLessonTitle(progress.getLesson().getTitle());
        r.setCompleted(progress.getCompleted());
        r.setXpEarned(progress.getXpEarned());
        r.setCompletedAt(progress.getCompletedAt());
        return r;
    }
}
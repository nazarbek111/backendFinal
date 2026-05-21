package kz.narxoz.backend.controller;

import kz.narxoz.backend.dto.request.ExerciseSubmitRequest;
import kz.narxoz.backend.dto.response.ChildBadgeResponse;
import kz.narxoz.backend.dto.response.LessonProgressResponse;
import kz.narxoz.backend.service.ProgressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ProgressController {

    private final ProgressService progressService;

    // Сдать упражнение
    @PostMapping("/exercises/{exerciseId}/submit")
    public ResponseEntity<String> submitExercise(
            @PathVariable Long exerciseId,
            @Valid @RequestBody ExerciseSubmitRequest request,
            @AuthenticationPrincipal String email) {
        return ResponseEntity.ok(
                progressService.submitExercise(exerciseId, request, email));
    }

    // Завершить урок
    @PostMapping("/lessons/{lessonId}/complete")
    public ResponseEntity<LessonProgressResponse> completeLesson(
            @PathVariable Long lessonId,
            @RequestParam Long childId,
            @AuthenticationPrincipal String email) {
        return ResponseEntity.ok(
                progressService.completeLesson(lessonId, childId, email));
    }

    // История прогресса ребёнка
    @GetMapping("/children/{childId}/progress")
    public ResponseEntity<List<LessonProgressResponse>> getProgress(
            @PathVariable Long childId,
            @AuthenticationPrincipal String email) {
        return ResponseEntity.ok(
                progressService.getProgress(childId, email));
    }

    // Значки ребёнка
    @GetMapping("/children/{childId}/badges")
    public ResponseEntity<List<ChildBadgeResponse>> getBadges(
            @PathVariable Long childId,
            @AuthenticationPrincipal String email) {
        return ResponseEntity.ok(
                progressService.getBadges(childId, email));
    }
}
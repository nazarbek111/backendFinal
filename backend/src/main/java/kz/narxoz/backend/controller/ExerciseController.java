package kz.narxoz.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kz.narxoz.backend.dto.request.ExerciseRequest;
import kz.narxoz.backend.dto.response.ExerciseResponse;
import kz.narxoz.backend.service.ExerciseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/exercises")
@RequiredArgsConstructor
@Tag(name = "Exercises", description = "Exercise management for lessons")
public class ExerciseController {

    private final ExerciseService exerciseService;

    @GetMapping("/lesson/{lessonId}")
    @Operation(summary = "Get all exercises for a lesson")
    public ResponseEntity<List<ExerciseResponse>> getExercisesByLesson(@PathVariable Long lessonId) {
        var pageResponse = exerciseService.getExercisesByLesson(lessonId, PageRequest.of(0, 100));
        return ResponseEntity.ok(pageResponse.getContent());
    }

    @PostMapping("/lesson/{lessonId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create an exercise for a lesson (Admin only)")
    public ResponseEntity<ExerciseResponse> createExercise(
            @PathVariable Long lessonId,
            @Valid @RequestBody ExerciseRequest request) {
        return ResponseEntity.status(201).body(exerciseService.createExercise(lessonId, request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update an exercise (Admin only)")
    public ResponseEntity<ExerciseResponse> updateExercise(
            @PathVariable Long id,
            @Valid @RequestBody ExerciseRequest request) {
        return ResponseEntity.ok(exerciseService.updateExercise(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete an exercise (Admin only)")
    public ResponseEntity<Void> deleteExercise(@PathVariable Long id) {
        exerciseService.deleteExercise(id);
        return ResponseEntity.noContent().build();
    }
}
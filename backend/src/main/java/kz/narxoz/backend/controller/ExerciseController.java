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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/exercises")
@RequiredArgsConstructor
@Tag(name = "Exercises", description = "Управление упражнениями для уроков")
public class ExerciseController {

    private final ExerciseService exerciseService;

    /**
     * ПОЛУЧИТЬ ВСЕ ЗАДАНИЯ УРОКА (Для игры)
     * Именно этот эндпоинт ты проверял в браузере. Он возвращает List (массив).
     */
    @GetMapping("/lesson/{lessonId}")
    @Operation(summary = "Получить все упражнения для конкретного урока")
    public ResponseEntity<List<ExerciseResponse>> getExercisesByLesson(@PathVariable Long lessonId) {
        // Мы берем первую страницу с запасом в 100 элементов, чтобы получить всё сразу
        var pageResponse = exerciseService.getExercisesByLesson(lessonId, PageRequest.of(0, 100));
        return ResponseEntity.ok(pageResponse.getContent());
    }

    /**
     * СОЗДАТЬ ЗАДАНИЕ
     */
    @PostMapping("/lesson/{lessonId}")
    @Operation(summary = "Создать новое упражнение для урока")
    public ResponseEntity<ExerciseResponse> createExercise(
            @PathVariable Long lessonId,
            @Valid @RequestBody ExerciseRequest request) {
        return ResponseEntity.status(201).body(exerciseService.createExercise(lessonId, request));
    }

    /**
     * ОБНОВИТЬ ЗАДАНИЕ
     */
    @PutMapping("/{id}")
    @Operation(summary = "Обновить существующее упражнение")
    public ResponseEntity<ExerciseResponse> updateExercise(
            @PathVariable Long id,
            @Valid @RequestBody ExerciseRequest request) {
        return ResponseEntity.ok(exerciseService.updateExercise(id, request));
    }

    /**
     * УДАЛИТЬ ЗАДАНИЕ
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить упражнение")
    public ResponseEntity<Void> deleteExercise(@PathVariable Long id) {
        exerciseService.deleteExercise(id);
        return ResponseEntity.noContent().build();
    }
}
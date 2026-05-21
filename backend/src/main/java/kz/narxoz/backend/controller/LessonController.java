package kz.narxoz.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kz.narxoz.backend.dto.request.LessonRequest;
import kz.narxoz.backend.dto.response.LessonResponse;
import kz.narxoz.backend.dto.response.PageResponse;
import kz.narxoz.backend.entity.enums.LessonType;
import kz.narxoz.backend.service.LessonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/lessons")
@RequiredArgsConstructor
@Tag(name = "Lessons")
public class LessonController {

    private final LessonService lessonService;

    @PostMapping
    @Operation(summary = "Create a lesson (Admin)")
    public ResponseEntity<LessonResponse> createLesson(@Valid @RequestBody LessonRequest request) {
        return ResponseEntity.status(201).body(lessonService.createLesson(request));
    }

    @GetMapping
    @Operation(summary = "List lessons with optional filters and pagination")
    public ResponseEntity<PageResponse<LessonResponse>> getAllLessons(
            @RequestParam(required = false) Long unitId,
            @RequestParam(required = false) LessonType type,
            @RequestParam(required = false) Boolean published,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(lessonService.getAllLessons(
                unitId, type, published,
                PageRequest.of(page, size, Sort.by("orderIndex"))));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a lesson by ID")
    public ResponseEntity<LessonResponse> getLesson(@PathVariable Long id) {
        return ResponseEntity.ok(lessonService.getLesson(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a lesson (Admin)")
    public ResponseEntity<LessonResponse> updateLesson(
            @PathVariable Long id,
            @Valid @RequestBody LessonRequest request) {
        return ResponseEntity.ok(lessonService.updateLesson(id, request));
    }

    @PatchMapping("/{id}/publish")
    @Operation(summary = "Publish a lesson (Admin)")
    public ResponseEntity<LessonResponse> publishLesson(@PathVariable Long id) {
        return ResponseEntity.ok(lessonService.publishLesson(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a lesson (Admin)")
    public ResponseEntity<Void> deleteLesson(@PathVariable Long id) {
        lessonService.deleteLesson(id);
        return ResponseEntity.noContent().build();
    }
}

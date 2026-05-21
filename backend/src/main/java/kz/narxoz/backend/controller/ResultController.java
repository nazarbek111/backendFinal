package kz.narxoz.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kz.narxoz.backend.dto.request.ExerciseSubmitRequest;
import kz.narxoz.backend.dto.response.ExerciseResultResponse;
import kz.narxoz.backend.service.ExerciseResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/results")
@RequiredArgsConstructor
@Tag(name = "Results", description = "Статистика и результаты прохождения")
public class ResultController {

    private final ExerciseResultService resultService;

    @PostMapping("/exercise/{exerciseId}")
    @Operation(summary = "Сохранить результат выполнения задания")
    public ResponseEntity<Void> saveResult(
            @PathVariable Long exerciseId,
            @Valid @RequestBody ExerciseSubmitRequest request) {
        resultService.saveResult(exerciseId, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/child/{childId}")
    public ResponseEntity<List<ExerciseResultResponse>> getResults(@PathVariable Long childId) {
        return ResponseEntity.ok(resultService.getChildResults(childId));
    }
}
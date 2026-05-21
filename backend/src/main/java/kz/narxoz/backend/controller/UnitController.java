package kz.narxoz.backend.controller;

import kz.narxoz.backend.dto.request.UnitRequest;
import kz.narxoz.backend.dto.response.UnitResponse;
import kz.narxoz.backend.service.UnitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/units")
@RequiredArgsConstructor
public class UnitController {

    private final UnitService unitService;

    @PostMapping
    public ResponseEntity<UnitResponse> createUnit(@Valid @RequestBody UnitRequest request) {
        return ResponseEntity.status(201).body(unitService.createUnit(request));
    }

    @GetMapping
    public ResponseEntity<List<UnitResponse>> getAllUnits() {
        return ResponseEntity.ok(unitService.getAllUnits());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UnitResponse> getUnit(@PathVariable Long id) {
        return ResponseEntity.ok(unitService.getUnit(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UnitResponse> updateUnit(
            @PathVariable Long id,
            @Valid @RequestBody UnitRequest request) {
        return ResponseEntity.ok(unitService.updateUnit(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUnit(@PathVariable Long id) {
        unitService.deleteUnit(id);
        return ResponseEntity.noContent().build();
    }
}
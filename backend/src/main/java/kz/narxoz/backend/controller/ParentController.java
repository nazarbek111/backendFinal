package kz.narxoz.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kz.narxoz.backend.dto.request.UpdateParentRequest;
import kz.narxoz.backend.dto.response.ChildResponse;
import kz.narxoz.backend.dto.response.ParentResponse;
import kz.narxoz.backend.service.ParentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/parents")
@RequiredArgsConstructor
@Tag(name = "Parents", description = "Parent profile management")
public class ParentController {

    private final ParentService parentService;

    @GetMapping("/{id}")
    @Operation(summary = "Get parent profile by ID")
    public ResponseEntity<ParentResponse> getParent(
            @PathVariable Long id,
            @AuthenticationPrincipal String email) {
        return ResponseEntity.ok(parentService.getParent(id, email));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update parent profile")
    public ResponseEntity<ParentResponse> updateParent(
            @PathVariable Long id,
            @Valid @RequestBody UpdateParentRequest request,
            @AuthenticationPrincipal String email) {
        return ResponseEntity.ok(parentService.updateParent(id, request, email));
    }

    @GetMapping("/{id}/children")
    @Operation(summary = "Get all children for a parent")
    public ResponseEntity<List<ChildResponse>> getChildren(
            @PathVariable Long id,
            @AuthenticationPrincipal String email) {
        return ResponseEntity.ok(parentService.getChildren(id, email));
    }
}
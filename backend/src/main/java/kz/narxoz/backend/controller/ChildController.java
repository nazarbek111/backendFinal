package kz.narxoz.backend.controller;

import kz.narxoz.backend.dto.request.ChildRequest;
import kz.narxoz.backend.dto.response.ChildResponse;
import kz.narxoz.backend.service.ChildService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/children")
@RequiredArgsConstructor
public class ChildController {

    private final ChildService childService;

    @PostMapping
    public ResponseEntity<ChildResponse> createChild(
            @Valid @RequestBody ChildRequest request,
            @AuthenticationPrincipal String email) {
        return ResponseEntity.status(201).body(childService.createChild(request, email));
    }

    @GetMapping
    public ResponseEntity<List<ChildResponse>> getMyChildren(
            @AuthenticationPrincipal String email) {
        return ResponseEntity.ok(childService.getMyChildren(email));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChildResponse> getChild(
            @PathVariable Long id,
            @AuthenticationPrincipal String email) {
        return ResponseEntity.ok(childService.getChild(id, email));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ChildResponse> updateChild(
            @PathVariable Long id,
            @Valid @RequestBody ChildRequest request,
            @AuthenticationPrincipal String email) {
        return ResponseEntity.ok(childService.updateChild(id, request, email));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChild(
            @PathVariable Long id,
            @AuthenticationPrincipal String email) {
        childService.deleteChild(id, email);
        return ResponseEntity.noContent().build();
    }
}
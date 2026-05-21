package kz.narxoz.backend.service;

import kz.narxoz.backend.dto.request.UnitRequest;
import kz.narxoz.backend.dto.response.UnitResponse;
import kz.narxoz.backend.entity.Unit;
import kz.narxoz.backend.repository.UnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UnitService {

    private final UnitRepository unitRepository;

    public UnitResponse createUnit(UnitRequest request) {
        Unit unit = Unit.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .orderIndex(request.getOrderIndex())
                .build();

        unitRepository.save(unit);
        return mapToResponse(unit);
    }

    public List<UnitResponse> getAllUnits() {
        return unitRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public UnitResponse getUnit(Long id) {
        Unit unit = unitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Unit not found"));
        return mapToResponse(unit);
    }

    public UnitResponse updateUnit(Long id, UnitRequest request) {
        Unit unit = unitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Unit not found"));

        unit.setTitle(request.getTitle());
        unit.setDescription(request.getDescription());
        unit.setOrderIndex(request.getOrderIndex());

        unitRepository.save(unit);
        return mapToResponse(unit);
    }

    public void deleteUnit(Long id) {
        unitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Unit not found"));
        unitRepository.deleteById(id);
    }

    private UnitResponse mapToResponse(Unit unit) {
        UnitResponse response = new UnitResponse();
        response.setId(unit.getId());
        response.setTitle(unit.getTitle());
        response.setDescription(unit.getDescription());
        response.setOrderIndex(unit.getOrderIndex());
        return response;
    }
}
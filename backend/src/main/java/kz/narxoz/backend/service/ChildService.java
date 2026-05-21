package kz.narxoz.backend.service;

import kz.narxoz.backend.dto.request.ChildRequest;
import kz.narxoz.backend.dto.response.ChildResponse;
import kz.narxoz.backend.entity.Child;
import kz.narxoz.backend.entity.Parent;
import kz.narxoz.backend.repository.ChildRepository;
import kz.narxoz.backend.repository.ParentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChildService {

    private final ChildRepository childRepository;
    private final ParentRepository parentRepository;

    public ChildResponse createChild(ChildRequest request, String parentEmail) {
        Parent parent = parentRepository.findByEmail(parentEmail)
                .orElseThrow(() -> new RuntimeException("Parent not found"));

        Child child = Child.builder()
                .name(request.getName())
                .age(request.getAge())
                .avatar(request.getAvatar())
                .parent(parent)
                .xpPoints(0)
                .level(1)
                .streak(0)
                .build();

        childRepository.save(child);
        return mapToResponse(child);
    }

    public List<ChildResponse> getMyChildren(String parentEmail) {
        Parent parent = parentRepository.findByEmail(parentEmail)
                .orElseThrow(() -> new RuntimeException("Parent not found"));

        return childRepository.findAllByParentId(parent.getId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public ChildResponse getChild(Long childId, String parentEmail) {
        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new RuntimeException("Child not found"));

        if (!child.getParent().getEmail().equals(parentEmail)) {
            throw new RuntimeException("Access denied");
        }

        return mapToResponse(child);
    }

    public ChildResponse updateChild(Long childId, ChildRequest request, String parentEmail) {
        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new RuntimeException("Child not found"));

        if (!child.getParent().getEmail().equals(parentEmail)) {
            throw new RuntimeException("Access denied");
        }

        child.setName(request.getName());
        child.setAge(request.getAge());
        child.setAvatar(request.getAvatar());

        childRepository.save(child);
        return mapToResponse(child);
    }

    public void deleteChild(Long childId, String parentEmail) {
        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new RuntimeException("Child not found"));

        if (!child.getParent().getEmail().equals(parentEmail)) {
            throw new RuntimeException("Access denied");
        }

        childRepository.delete(child);
    }

    private ChildResponse mapToResponse(Child child) {
        ChildResponse response = new ChildResponse();
        response.setId(child.getId());
        response.setName(child.getName());
        response.setAge(child.getAge());
        response.setAvatar(child.getAvatar());
        response.setXpPoints(child.getXpPoints());
        response.setLevel(child.getLevel());
        response.setStreak(child.getStreak());
        response.setLastActiveDate(child.getLastActiveDate());
        return response;
    }
}
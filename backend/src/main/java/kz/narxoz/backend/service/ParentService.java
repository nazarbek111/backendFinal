package kz.narxoz.backend.service;

import kz.narxoz.backend.dto.request.UpdateParentRequest;
import kz.narxoz.backend.dto.response.ChildResponse;
import kz.narxoz.backend.dto.response.ParentResponse;
import kz.narxoz.backend.entity.Child;
import kz.narxoz.backend.entity.Parent;
import kz.narxoz.backend.repository.ChildRepository;
import kz.narxoz.backend.repository.ParentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ParentService {

    private final ParentRepository parentRepository;
    private final ChildRepository childRepository;
    private final PasswordEncoder passwordEncoder;

    public ParentResponse getParent(Long id, String email) {
        Parent parent = parentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Parent not found"));

        // Parents can only view their own profile
        if (!parent.getEmail().equals(email)) {
            throw new RuntimeException("Access denied");
        }

        return mapToResponse(parent);
    }

    @Transactional
    public ParentResponse updateParent(Long id, UpdateParentRequest request, String email) {
        Parent parent = parentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Parent not found"));

        if (!parent.getEmail().equals(email)) {
            throw new RuntimeException("Access denied");
        }

        parent.setName(request.getName());

        // Only update password if a new one was provided
        if (request.getNewPassword() != null && !request.getNewPassword().isBlank()) {
            parent.setPassword(passwordEncoder.encode(request.getNewPassword()));
        }

        parentRepository.save(parent);
        return mapToResponse(parent);
    }

    public List<ChildResponse> getChildren(Long parentId, String email) {
        Parent parent = parentRepository.findById(parentId)
                .orElseThrow(() -> new RuntimeException("Parent not found"));

        if (!parent.getEmail().equals(email)) {
            throw new RuntimeException("Access denied");
        }

        return childRepository.findAllByParentId(parentId)
                .stream()
                .map(this::mapChildToResponse)
                .collect(Collectors.toList());
    }

    private ParentResponse mapToResponse(Parent parent) {
        ParentResponse r = new ParentResponse();
        r.setId(parent.getId());
        r.setName(parent.getName());
        r.setEmail(parent.getEmail());
        r.setRole(parent.getRole().name());
        r.setCreatedAt(parent.getCreatedAt());
        return r;
    }

    private ChildResponse mapChildToResponse(Child child) {
        ChildResponse r = new ChildResponse();
        r.setId(child.getId());
        r.setName(child.getName());
        r.setAge(child.getAge());
        r.setAvatar(child.getAvatar());
        r.setXpPoints(child.getXpPoints());
        r.setLevel(child.getLevel());
        r.setStreak(child.getStreak());
        r.setLastActiveDate(child.getLastActiveDate());
        return r;
    }
}
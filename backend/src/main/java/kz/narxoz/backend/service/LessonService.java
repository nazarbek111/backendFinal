package kz.narxoz.backend.service;

import kz.narxoz.backend.dto.request.LessonRequest;
import kz.narxoz.backend.dto.response.LessonResponse;
import kz.narxoz.backend.dto.response.PageResponse;
import kz.narxoz.backend.entity.Lesson;
import kz.narxoz.backend.entity.Unit;
import kz.narxoz.backend.entity.enums.LessonType;
import kz.narxoz.backend.repository.LessonRepository;
import kz.narxoz.backend.repository.UnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LessonService {

    private final LessonRepository lessonRepository;
    private final UnitRepository unitRepository;

    public LessonResponse createLesson(LessonRequest request) {
        Unit unit = unitRepository.findById(request.getUnitId())
                .orElseThrow(() -> new RuntimeException("Unit not found"));

        Lesson lesson = Lesson.builder()
                .title(request.getTitle())
                .type(request.getType())
                .unit(unit)
                .orderIndex(request.getOrderIndex())
                .xpReward(request.getXpReward() != null ? request.getXpReward() : 10)
                .published(false)
                .build();

        lessonRepository.save(lesson);
        return mapToResponse(lesson);
    }

    public PageResponse<LessonResponse> getAllLessons(Long unitId, LessonType type,
                                                      Boolean published, Pageable pageable) {
        Page<Lesson> page = lessonRepository.findWithFilters(unitId, type, published, pageable);
        return new PageResponse<>(
                page.getContent().stream().map(this::mapToResponse).toList(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.getNumber(),
                page.getSize()
        );
    }

    public LessonResponse getLesson(Long id) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lesson not found"));
        return mapToResponse(lesson);
    }

    public LessonResponse updateLesson(Long id, LessonRequest request) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lesson not found"));
        Unit unit = unitRepository.findById(request.getUnitId())
                .orElseThrow(() -> new RuntimeException("Unit not found"));

        lesson.setTitle(request.getTitle());
        lesson.setType(request.getType());
        lesson.setUnit(unit);
        lesson.setOrderIndex(request.getOrderIndex());
        lesson.setXpReward(request.getXpReward());

        lessonRepository.save(lesson);
        return mapToResponse(lesson);
    }

    public LessonResponse publishLesson(Long id) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lesson not found"));
        lesson.setPublished(true);
        lessonRepository.save(lesson);
        return mapToResponse(lesson);
    }

    public void deleteLesson(Long id) {
        lessonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lesson not found"));
        lessonRepository.deleteById(id);
    }

    private LessonResponse mapToResponse(Lesson lesson) {
        LessonResponse response = new LessonResponse();
        response.setId(lesson.getId());
        response.setTitle(lesson.getTitle());
        response.setType(lesson.getType());
        response.setPublished(lesson.getPublished());
        response.setXpReward(lesson.getXpReward());
        response.setOrderIndex(lesson.getOrderIndex());
        response.setUnitId(lesson.getUnit().getId());
        return response;
    }
}

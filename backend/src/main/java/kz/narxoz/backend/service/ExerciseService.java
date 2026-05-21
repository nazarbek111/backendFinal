package kz.narxoz.backend.service;

import kz.narxoz.backend.dto.request.ExerciseRequest;
import kz.narxoz.backend.dto.response.ExerciseResponse;
import kz.narxoz.backend.dto.response.PageResponse;
import kz.narxoz.backend.entity.Exercise;
import kz.narxoz.backend.entity.Lesson;
import kz.narxoz.backend.repository.ExerciseRepository;
import kz.narxoz.backend.repository.LessonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExerciseService {

    private final ExerciseRepository exerciseRepository;
    private final LessonRepository lessonRepository;

    public PageResponse<ExerciseResponse> getExercisesByLesson(Long lessonId, Pageable pageable) {

        Page<Exercise> page = exerciseRepository.findByLessonId(lessonId, pageable);

        return new PageResponse<>(
                page.getContent().stream().map(this::mapToResponse).toList(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.getNumber(),
                page.getSize()
        );
    }

    public ExerciseResponse createExercise(Long lessonId, ExerciseRequest request) {

        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Lesson not found"));

        Exercise exercise = new Exercise();
        exercise.setLesson(lesson);
        exercise.setQuestion(request.getQuestion());
        exercise.setCorrectAnswer(request.getCorrectAnswer());
        exercise.setOptions(request.getOptions());
        exercise.setOrderIndex(request.getOrderIndex());

        return mapToResponse(exerciseRepository.save(exercise));
    }

    public ExerciseResponse updateExercise(Long id, ExerciseRequest request) {

        Exercise exercise = exerciseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Exercise not found"));

        exercise.setQuestion(request.getQuestion());
        exercise.setCorrectAnswer(request.getCorrectAnswer());
        exercise.setOptions(request.getOptions());
        exercise.setOrderIndex(request.getOrderIndex());

        return mapToResponse(exerciseRepository.save(exercise));
    }

    public void deleteExercise(Long id) {
        exerciseRepository.deleteById(id);
    }

    private ExerciseResponse mapToResponse(Exercise exercise) {

        ExerciseResponse response = new ExerciseResponse();
        response.setId(exercise.getId());
        response.setLessonId(exercise.getLesson().getId());
        response.setQuestion(exercise.getQuestion());
        response.setCorrectAnswer(exercise.getCorrectAnswer());
        response.setOptions(exercise.getOptions());
        response.setOrderIndex(exercise.getOrderIndex());

        return response;
    }
}
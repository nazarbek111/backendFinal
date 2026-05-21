package kz.narxoz.backend.service;

import kz.narxoz.backend.dto.request.ExerciseSubmitRequest;
import kz.narxoz.backend.dto.response.ExerciseResultResponse;
import kz.narxoz.backend.entity.Exercise;
import kz.narxoz.backend.entity.ExerciseResult;
import kz.narxoz.backend.repository.ChildRepository;
import kz.narxoz.backend.repository.ExerciseRepository;
import kz.narxoz.backend.repository.ExerciseResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExerciseResultService {

    private final ExerciseResultRepository resultRepository;
    private final ChildRepository childRepository;
    private final ExerciseRepository exerciseRepository;

    @Transactional
    public void saveResult(Long exerciseId, ExerciseSubmitRequest request) {
        var child = childRepository.findById(request.getChildId())
                .orElseThrow(() -> new RuntimeException("Ребенок не найден с ID: " + request.getChildId()));

        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new RuntimeException("Упражнение не найдено с ID: " + exerciseId));

        boolean isCorrect = exercise.getCorrectAnswer()
                .trim()
                .equalsIgnoreCase(request.getAnswer().trim());

        ExerciseResult result = ExerciseResult.builder()
                .child(child)
                .exercise(exercise)
                .correct(isCorrect)
                .timeTaken(request.getTimeTaken())
                .build();

        resultRepository.save(result);
    }

    public List<ExerciseResultResponse> getChildResults(Long childId) {
        List<ExerciseResult> results = resultRepository.findAllByChildIdOrderBySubmittedAtDesc(childId);

        return results.stream().map(r -> new ExerciseResultResponse(
                r.getId(),
                r.getExercise().getQuestion(),
                r.getCorrect(),
                r.getTimeTaken(),
                r.getSubmittedAt()
        )).collect(Collectors.toList());
    }
}
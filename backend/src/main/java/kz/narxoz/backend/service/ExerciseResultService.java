package kz.narxoz.backend.service;

import kz.narxoz.backend.dto.request.ExerciseSubmitRequest;
import kz.narxoz.backend.dto.response.ExerciseResultResponse;
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
    public void saveResult(ExerciseSubmitRequest request) {
        // Ищем ребенка по ID
        var child = childRepository.findById(request.getChildId())
                .orElseThrow(() -> new RuntimeException("Ребенок не найден с ID: " + request.getChildId()));

        // Ищем упражнение по ID
        var exercise = exerciseRepository.findById(request.getExerciseId())
                .orElseThrow(() -> new RuntimeException("Упражнение не найдено с ID: " + request.getExerciseId()));

        // Собираем сущность через Builder (он у тебя есть в Entity)
        ExerciseResult result = ExerciseResult.builder()
                .child(child)
                .exercise(exercise)
                .correct(request.getCorrect())
                .timeTaken(request.getTimeTaken())
                .build();

        resultRepository.save(result);
    }

    // Метод для получения истории (для родителя)
    public List<ExerciseResultResponse> getChildResults(Long childId) {
        List<ExerciseResult> results = resultRepository.findAllByChildIdOrderBySubmittedAtDesc(childId);

        return results.stream().map(r -> new ExerciseResultResponse(
                r.getId(),
                r.getExercise().getQuestion(), // Берем только строку вопроса
                r.getCorrect(),
                r.getTimeTaken(),
                r.getSubmittedAt()
        )).collect(Collectors.toList());
    }
}
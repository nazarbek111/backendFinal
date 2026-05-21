package kz.narxoz.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ExerciseResultResponse {
    private Long id;
    private String exerciseQuestion; // Только текст вопроса
    private boolean correct;
    private Integer timeTaken;
    private LocalDateTime submittedAt;
}
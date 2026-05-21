package kz.narxoz.backend.dto.response;

import lombok.Data;

@Data
public class ExerciseResponse {

    private Long id;
    private Long lessonId;
    private String question;
    private String correctAnswer;
    private String options;
    private Integer orderIndex;
}
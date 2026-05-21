package kz.narxoz.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ExerciseRequest {

    @NotBlank
    private String question;

    @NotBlank
    private String correctAnswer;

    private String options;

    private Integer orderIndex;
}
package kz.narxoz.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ExerciseSubmitRequest {

    @NotNull
    private Long childId;

    @NotBlank
    private String answer;

    private Integer timeTaken;
}
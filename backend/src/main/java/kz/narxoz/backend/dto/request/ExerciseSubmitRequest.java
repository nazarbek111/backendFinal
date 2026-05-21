package kz.narxoz.backend.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ExerciseSubmitRequest {
    @NotNull
    private Long childId;

    @NotNull
    private Long exerciseId;

    @NotNull
    private Boolean correct;

    private Integer timeTaken;
}
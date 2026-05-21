package kz.narxoz.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import kz.narxoz.backend.entity.enums.LessonType;
import lombok.Data;

@Data
public class LessonRequest {
    @NotBlank
    private String title;

    @NotNull
    private LessonType type;

    @NotNull
    private Long unitId;

    private Integer orderIndex;
    private Integer xpReward = 10;
}
package kz.narxoz.backend.dto.response;

import kz.narxoz.backend.entity.enums.LessonType;
import lombok.Data;

@Data
public class LessonResponse {
    private Long id;
    private String title;
    private LessonType type;
    private Boolean published;
    private Integer xpReward;
    private Integer orderIndex;
    private Long unitId;
}
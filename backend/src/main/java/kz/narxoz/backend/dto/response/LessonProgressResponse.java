package kz.narxoz.backend.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class LessonProgressResponse {
    private Long id;
    private Long lessonId;
    private String lessonTitle;
    private Boolean completed;
    private Integer xpEarned;
    private LocalDateTime completedAt;
}
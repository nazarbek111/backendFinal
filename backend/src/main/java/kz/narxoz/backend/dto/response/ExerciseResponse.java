package kz.narxoz.backend.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
public class ExerciseResponse {

    private Long id;
    private Long lessonId;
    private String question;
    private String options;
    private Integer orderIndex;

}
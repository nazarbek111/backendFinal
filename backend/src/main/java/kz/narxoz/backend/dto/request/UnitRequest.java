package kz.narxoz.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UnitRequest {
    @NotBlank
    private String title;

    private String description;
    private Integer orderIndex;
}
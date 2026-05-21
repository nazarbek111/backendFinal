package kz.narxoz.backend.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ChildRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Age is required")
    @Min(value = 3, message = "Child must be at least 3 years old")
    @Max(value = 8, message = "Child must be at most 8 years old")
    private Integer age;

    private String avatar;
}
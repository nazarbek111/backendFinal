package kz.narxoz.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChildRequest {
    @NotBlank
    private String name;

    @NotNull
    private Integer age;

    private String avatar;
}
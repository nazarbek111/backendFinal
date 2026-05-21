package kz.narxoz.backend.dto.response;

import lombok.Data;

@Data
public class UnitResponse {
    private Long id;
    private String title;
    private String description;
    private Integer orderIndex;
}
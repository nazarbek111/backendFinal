package kz.narxoz.backend.dto.response;

import lombok.Data;

@Data
public class ChildResponse {
    private Long id;
    private String name;
    private Integer age;
    private String avatar;
    private Integer xpPoints;
    private Integer level;
    private Integer streak;
}
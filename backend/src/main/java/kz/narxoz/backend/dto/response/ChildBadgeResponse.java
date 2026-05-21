package kz.narxoz.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ChildBadgeResponse {
    private String name;
    private String description;
    private String icon;
    private LocalDateTime earnedAt;
}
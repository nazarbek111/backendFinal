package kz.narxoz.backend.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ParentResponse {
    private Long id;
    private String name;
    private String email;
    private String role;
    private LocalDateTime createdAt;
}
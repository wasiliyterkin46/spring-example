package io.hexlet.spring.dto.post;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostDTO {
    private Long id;
    private String title;
    private String content;
    private Boolean published;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

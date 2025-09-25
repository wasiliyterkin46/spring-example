package io.hexlet.spring.dto.post;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostUpdateDTO {
    @NotBlank
    private String title;
    @NotBlank
    private String content;
}

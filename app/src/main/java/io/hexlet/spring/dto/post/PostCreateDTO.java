package io.hexlet.spring.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostCreateDTO {
    @NotBlank
    private String title;
    @NotBlank
    private String content;
    @NotNull
    private Boolean published;
}

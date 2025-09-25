package io.hexlet.spring.dto.post;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

@Getter
@Setter
public class PostPatchDTO {
    @NotBlank
    private JsonNullable<String> title;
    @NotBlank
    private JsonNullable<String> content;
}

package io.hexlet.spring.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

@Setter
@Getter
public class UserPatchDTO {
    @NotNull
    @NotBlank
    private JsonNullable<String> email = JsonNullable.undefined();
    private JsonNullable<String> firstName = JsonNullable.undefined();
}

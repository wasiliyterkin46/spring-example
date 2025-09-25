package io.hexlet.spring.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Setter;
import lombok.Getter;

@Setter
@Getter
public class UserUpdateDTO {
    @NotNull
    @NotBlank
    private String email;
    private String firstName;
}

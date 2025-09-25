package io.hexlet.spring.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

import lombok.Setter;
import lombok.Getter;

@Setter
@Getter
public class UserCreateDTO {
    @NotBlank
    @NotNull
    private String email;
    private String firstName;
    private String lastName;
    private LocalDate birthday;
}

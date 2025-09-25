package io.hexlet.spring.dto.user;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Setter;
import lombok.Getter;

@Setter
@Getter
public class UserDTO {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private LocalDate birthday;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

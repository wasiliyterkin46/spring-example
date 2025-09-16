package io.hexlet.spring.model;

import jakarta.validation.constraints.NotBlank;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class User {
    private Long id;
    private String name;
    @NotBlank
    private String email;

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public void update(User user) {
        if (user.getEmail() != null) {
            this.setEmail(user.getEmail());
        }
        if (user.getName() != null) {
            this.setName(user.getName());
        }
    }
}

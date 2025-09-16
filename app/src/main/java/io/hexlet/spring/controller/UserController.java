package io.hexlet.spring.controller;

import io.hexlet.spring.model.User;
import jakarta.validation.Valid;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@RestController
@RequestMapping("/api/users")
public class UserController {
    private List<User> users = new ArrayList<>();

    @GetMapping
    public ResponseEntity<List<User>> index(@RequestParam(defaultValue = "10") Integer limit) {
        var list = users.stream().limit(limit).toList();
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(users.size()))
                .body(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> show(@PathVariable String id) {
        return users.stream()
                .filter(p -> p.getId().equals(Long.parseLong(id)))
                .findFirst()
                .map(user -> ResponseEntity.ok().body(user)).orElseGet(() ->
                        ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    @PostMapping
    public ResponseEntity<User> create(@Valid @RequestBody User user) {
        user.setId((long) (users.size() + 1));
        users.add(user);
        return ResponseEntity.status(201).body(user);
    }

    @PutMapping("/{id}") // Обновление страницы
    public ResponseEntity<User> update(@PathVariable String id, @Valid @RequestBody User data) {
        var maybePost = users.stream()
                .filter(p -> p.getId().equals(Long.parseLong(id)))
                .findFirst();
        User user = null;
        if (maybePost.isPresent()) {
            user = maybePost.get();
            user.update(data);
            return ResponseEntity.ok().body(user);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> destroy(@PathVariable String id) {
        users.removeIf(p -> p.getId().equals(Long.parseLong(id)));
        return ResponseEntity.status(204).build();
    }
}
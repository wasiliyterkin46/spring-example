package io.hexlet.spring.controller;

import io.hexlet.spring.model.User;
import io.hexlet.spring.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
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

import java.util.List;

import static io.hexlet.utils.UpdateEntity.updateEntity;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<List<User>> index(@RequestParam(defaultValue = "10") Integer limit,
                                            @RequestParam(defaultValue = "0") Integer offset) {
        var users = userRepository.findAll(PageRequest.of(offset, limit));
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(userRepository.count()))
                .body(users.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> show(@PathVariable String id) {
        var longId = Long.parseLong(id);
        var user = userRepository.findById(longId);
        if (user.isPresent()) {
            return ResponseEntity.ok().body(user.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    @PostMapping
    public ResponseEntity<User> create(@Valid @RequestBody User user) {
        userRepository.save(user);
        return ResponseEntity.status(201).body(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> update(@PathVariable String id, @Valid @RequestBody User data) throws NoSuchFieldException, IllegalAccessException {
        var longId = Long.parseLong(id);
        var findedUser = userRepository.findById(longId).orElseThrow(EntityNotFoundException::new);
        updateEntity(findedUser, data);
        userRepository.save(findedUser);

        return ResponseEntity.ok().body(findedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> destroy(@PathVariable String id) {
        var longId = Long.parseLong(id);
        userRepository.deleteById(longId);
        return ResponseEntity.status(204).build();
    }
}
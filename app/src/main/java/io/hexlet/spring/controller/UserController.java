package io.hexlet.spring.controller;

import io.hexlet.spring.exception.ResourceAlreadyExistsException;
import io.hexlet.spring.exception.ResourceNotFoundException;
import io.hexlet.spring.model.User;
import io.hexlet.spring.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
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

import static io.hexlet.spring.utils.UpdateEntity.updateEntity;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<List<User>> index(@RequestParam(defaultValue = "10") Integer limit,
                                            @RequestParam(defaultValue = "1") Integer page) {
        var users = userRepository.findAll(PageRequest.of(page - 1, limit));
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(userRepository.count()))
                .body(users.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> show(@PathVariable long id) {
        var user = userRepository.findById(id);
        if (user.isPresent()) {
            return ResponseEntity.ok().body(user.get());
        } else {
            throw new ResourceNotFoundException(String.format("User with id = %s not found", id));
        }
    }

    @PostMapping
    public ResponseEntity<User> create(@Valid @RequestBody User user) {
        if (userRepository.findAll().contains(user)) {
            throw new ResourceAlreadyExistsException("User already exist");
        }
        return ResponseEntity.status(201).body(userRepository.save(user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> update(@PathVariable long id, @Valid @RequestBody User data) throws NoSuchFieldException, IllegalAccessException {
        var findedUser = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("User with id = %s not found", id)));
        updateEntity(findedUser, data);
        userRepository.save(findedUser);

        return ResponseEntity.ok().body(findedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> destroy(@PathVariable long id) {
        if (userRepository.findById(id).isEmpty()) {
            throw new ResourceNotFoundException(String.format("User with id = %s not found", id));
        }
        userRepository.deleteById(id);
        return ResponseEntity.status(204).build();
    }
}
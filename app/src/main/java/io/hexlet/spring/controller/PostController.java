package io.hexlet.spring.controller;

import io.hexlet.spring.model.Post;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SpringBootApplication
@RestController
public class PostController {
    private List<Post> posts = new ArrayList<>();

    @GetMapping("/posts")
    public List<Post> index(@RequestParam(defaultValue = "10") Integer limit) {
        return posts.stream().limit(limit).toList();
    }

    @GetMapping("/posts/{id}")
    public Optional<Post> show(@PathVariable String id) {
        return posts.stream()
                .filter(p -> p.getId().equals(Long.parseLong(id)))
                .findFirst();
    }

    @PostMapping("/posts")
    public ResponseEntity<Post> create(@Valid @RequestBody Post post) {
        post.setId((long) (posts.size() + 1));
        post.setCreatedAt(LocalDateTime.now());
        posts.add(post);
        return ResponseEntity.status(201).body(post);
    }

    @PutMapping("/posts/{id}") // Обновление страницы
    public Post update(@PathVariable String id, @Valid @RequestBody Post data) {
        var maybePost = posts.stream()
                .filter(p -> p.getId().equals(Long.parseLong(id)))
                .findFirst();
        Post post = null;
        if (maybePost.isPresent()) {
            post = maybePost.get();
            post.update(data);
        }
        return post;
    }

    @DeleteMapping("/posts/{id}")
    public ResponseEntity<Void> destroy(@PathVariable String id) {
        posts.removeIf(p -> p.getId().equals(Long.parseLong(id)));
        return ResponseEntity.status(204).build();
    }
}

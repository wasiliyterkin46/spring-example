package io.hexlet.spring.controller;

import io.hexlet.spring.exception.ResourceNotFoundException;
import io.hexlet.spring.model.Post;
import io.hexlet.spring.repository.PostRepository;
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

import static io.hexlet.utils.UpdateEntity.updateEntity;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    @Autowired
    private PostRepository postRepository;

    public PostController(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @GetMapping
    public ResponseEntity<List<Post>> index(@RequestParam(defaultValue = "10") Integer limit, @RequestParam(defaultValue = "0") Integer offset) {
        var posts = postRepository.findAll(PageRequest.of(offset, limit));
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(postRepository.count()))
                .body(posts.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> show(@PathVariable String id) {
        var longId = Long.parseLong(id);
        var post = postRepository.findById(longId);
        if (post.isPresent()) {
            return ResponseEntity.ok().body(post.get());
        } else {
            throw new ResourceNotFoundException(String.format("Post with id = %s not found", id));
        }
//        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    @PostMapping
    public ResponseEntity<Post> create(@Valid @RequestBody Post post) {
        postRepository.save(post);
        return ResponseEntity.status(201).body(post);
    }

    @PutMapping("/{id}") // Обновление страницы
    public ResponseEntity<Post> update(@PathVariable String id, @Valid @RequestBody Post data) throws NoSuchFieldException, IllegalAccessException {
        var longId = Long.parseLong(id);
        var findedPost = postRepository.findById(longId).orElseThrow(() -> new ResourceNotFoundException(String.format("Post with id = %s not found", id)));
        updateEntity(findedPost, data);
        postRepository.save(findedPost);

        return ResponseEntity.ok().body(findedPost);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> destroy(@PathVariable String id) {
        var longId = Long.parseLong(id);
        if (postRepository.findById(longId).isEmpty()) {
            throw new ResourceNotFoundException(String.format("Post with id = %s not found", id));
        }
        postRepository.deleteById(longId);
        return ResponseEntity.status(204).build();
    }
}

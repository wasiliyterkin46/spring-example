package io.hexlet.spring.controller;

import io.hexlet.spring.exception.ResourceAlreadyExistsException;
import io.hexlet.spring.exception.ResourceNotFoundException;
import io.hexlet.spring.model.Post;
import io.hexlet.spring.repository.PostRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static io.hexlet.spring.utils.UpdateEntity.updateEntity;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    @Autowired
    private PostRepository postRepository;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<Post> getPublishedPosts(@RequestParam(defaultValue = "10") Integer limit,
                    @RequestParam(defaultValue = "1") Integer page,
                    @RequestParam(defaultValue = "false") Boolean isPublished) {
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by("createdAt").descending());
        return isPublished ? postRepository.findByPublishedTrue(pageable) : postRepository.findAll(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> show(@PathVariable long id) {
        var post = postRepository.findById(id);
        if (post.isPresent()) {
            return ResponseEntity.ok().body(post.get());
        } else {
            throw new ResourceNotFoundException(String.format("Post with id = %s not found", id));
        }
    }

    @PostMapping
    public ResponseEntity<Post> create(@Valid @RequestBody Post post) {
        if (postRepository.findAll().contains(post)) {
            throw new ResourceAlreadyExistsException("Post already exist");
        }
        return ResponseEntity.status(201).body(postRepository.save(post));
    }

    @PutMapping("/{id}") // Обновление страницы
    public ResponseEntity<Post> update(@PathVariable long id, @Valid @RequestBody Post data) throws NoSuchFieldException, IllegalAccessException {
        var findedPost = postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("Post with id = %s not found", id)));
        updateEntity(findedPost, data);
        postRepository.save(findedPost);

        return ResponseEntity.ok().body(findedPost);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> destroy(@PathVariable long id) {
        if (postRepository.findById(id).isEmpty()) {
            throw new ResourceNotFoundException(String.format("Post with id = %s not found", id));
        }
        postRepository.deleteById(id);
        return ResponseEntity.status(204).build();
    }
}

package io.hexlet.spring.controller.api;

import io.hexlet.spring.dto.post.PostCreateDTO;
import io.hexlet.spring.dto.post.PostDTO;
import io.hexlet.spring.dto.post.PostMapper;
import io.hexlet.spring.dto.post.PostMapperPatch;
import io.hexlet.spring.dto.post.PostPatchDTO;
import io.hexlet.spring.dto.post.PostUpdateDTO;
import io.hexlet.spring.exception.ResourceAlreadyExistsException;
import io.hexlet.spring.exception.ResourceNotFoundException;
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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/posts")
public class PostsController {
    @Autowired
    private PostRepository repository;

    @Autowired
    private PostMapper mapper;

    @Autowired
    private PostMapperPatch mapperPatch;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<PostDTO> index(@RequestParam(defaultValue = "10") Integer limit,
                               @RequestParam(defaultValue = "1") Integer page,
                               @RequestParam(defaultValue = "false") Boolean isPublishedOnly) {
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by("createdAt").descending());
        var postInPage =  isPublishedOnly ? repository.findByPublishedTrue(pageable) : repository.findAll(pageable);
        return postInPage.map(p -> mapper.map(p));
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PostDTO show(@PathVariable long id) {
        var post = repository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException(String.format("Post with id = %s not found", id)));

        return mapper.map(post);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PostDTO create(@Valid @RequestBody PostCreateDTO data) {
        var post = mapper.map(data);
        if (repository.findAll().contains(post)) {
            throw new ResourceAlreadyExistsException("Post already exist");
        }
        repository.save(post);
        return mapper.map(post);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PostDTO update(@PathVariable long id,
                @Valid @RequestBody PostUpdateDTO data) throws NoSuchFieldException, IllegalAccessException {
        var post = repository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException(String.format("Post with id = %s not found", id)));
        mapper.update(data, post);
        repository.save(post);

        return mapper.map(post);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void destroy(@PathVariable long id) {
        if (repository.findById(id).isEmpty()) {
            throw new ResourceNotFoundException(String.format("Post with id = %s not found", id));
        }
        repository.deleteById(id);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<PostDTO> patch(@PathVariable Long id,
                                             @RequestBody PostPatchDTO dto) {
        var post = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Post with id = %s not found", id)));

        mapperPatch.update(dto, post);
        repository.save(post);
        return ResponseEntity.ok(mapper.map(post));
    }
}

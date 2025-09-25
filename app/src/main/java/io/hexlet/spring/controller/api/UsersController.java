package io.hexlet.spring.controller.api;

import io.hexlet.spring.dto.user.UserCreateDTO;
import io.hexlet.spring.dto.user.UserDTO;
import io.hexlet.spring.dto.user.UserMapper;
import io.hexlet.spring.dto.user.UserMapperPatch;
import io.hexlet.spring.dto.user.UserPatchDTO;
import io.hexlet.spring.dto.user.UserUpdateDTO;
import io.hexlet.spring.exception.ResourceAlreadyExistsException;
import io.hexlet.spring.exception.ResourceNotFoundException;
import io.hexlet.spring.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
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
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UsersController {
    @Autowired
    private UserRepository repository;

    @Autowired
    private UserMapper mapper;

    @Autowired
    private UserMapperPatch mapperPatch;

    @GetMapping
    public ResponseEntity<List<UserDTO>> index(@RequestParam(defaultValue = "10") Integer limit,
                                               @RequestParam(defaultValue = "1") Integer page) {
        var users = repository.findAll(PageRequest.of(page - 1, limit));
        var resultList = users.toList().stream()
                .map(u -> mapper.map(u))
                .toList();
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(repository.count()))
                .body(resultList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> show(@PathVariable long id) {
        var user = repository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException(String.format("User with id = %s not found", id)));

        return ResponseEntity.ok().body(mapper.map(user));
    }

    @PostMapping
    public ResponseEntity<UserDTO> create(@Valid @RequestBody UserCreateDTO data) {
        var user = mapper.map(data);
        if (repository.findAll().contains(user)) {
            throw new ResourceAlreadyExistsException("User already exist");
        }
        repository.save(user);
        var resultBody = mapper.map(user);
        return ResponseEntity.status(201).body(resultBody);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> update(@PathVariable long id,
                   @Valid @RequestBody UserUpdateDTO data) throws NoSuchFieldException, IllegalAccessException {
        var findedUser = repository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException(String.format("User with id = %s not found", id)));
        mapper.update(data, findedUser);
        repository.save(findedUser);

        return ResponseEntity.ok().body(mapper.map(findedUser));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> destroy(@PathVariable long id) {
        if (repository.findById(id).isEmpty()) {
            throw new ResourceNotFoundException(String.format("User with id = %s not found", id));
        }
        repository.deleteById(id);
        return ResponseEntity.status(204).build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserDTO> patch(@PathVariable Long id,
                                             @RequestBody UserPatchDTO dto) {
        var user = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("User with id = %s not found", id)));

        mapperPatch.update(dto, user);
        repository.save(user);
        return ResponseEntity.ok(mapper.map(user));
    }

}

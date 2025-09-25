package io.hexlet.spring.controller.api;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.hexlet.spring.model.Post;
import io.hexlet.spring.repository.PostRepository;
import net.datafaker.Faker;
import net.javacrumbs.jsonunit.core.Option;
import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;

@SpringBootTest
@AutoConfigureMockMvc
public class PostsControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Faker faker;

    @Autowired
    private PostRepository repository;

    @Autowired
    private ObjectMapper om;

    @BeforeEach
    void clearRepository() {
        repository.deleteAll();
    }

    @Test
    public void testDelete() throws Exception {
        System.out.println(om.writeValueAsString(repository.findAll()));
        // Добавить пользователей
        Long idPost = 0L;
        for (int x = 1; x <= 3; x++) {
            var post = Instancio.of(Post.class)
                    .ignore(Select.field(Post::getId))
                    .supply(Select.field(Post::getTitle), () -> faker.name().firstName())
                    .supply(Select.field(Post::getContent), () -> faker.name().lastName())
                    .supply(Select.field(Post::getPublished), () -> true)
                    .create();
            repository.save(post);
            idPost = post.getId();
        }
        var result = mockMvc.perform(delete("/api/posts/" + idPost))
                .andExpect(status().isNoContent())
                .andReturn();

        var users = repository.findAll();
        assertEquals(2, users.size());
        Long finalIdPost = idPost;
        assertTrue(users.stream()
                .filter(u -> u.getId() == finalIdPost)
                .count() == 0);
    }

    @Test
    public void testCreate() throws Exception {
        var post = Instancio.of(Post.class)
                .ignore(Select.field(Post::getId))
                .ignore(Select.field(Post::getCreatedAt))
                .ignore(Select.field(Post::getUpdatedAt))
                .supply(Select.field(Post::getTitle), () -> faker.name().firstName())
                .supply(Select.field(Post::getContent), () -> faker.name().lastName())
                .supply(Select.field(Post::getPublished), () -> true)
                .create();

        var request = post("/api/posts")
                .contentType(MediaType.APPLICATION_JSON)
                // ObjectMapper конвертирует Map в JSON
                .content(om.writeValueAsString(post));

        var result = mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isObject();

        var viewPost = om.writeValueAsString(post);
        assertThatJson(body)
                .whenIgnoringPaths("updatedAt", "createdAt", "id")
                .isEqualTo(viewPost);
    }

    @Test
    public void testIndexAll() throws Exception {
        // Добавить пользователей
        for (int x = 1; x <= 3; x++) {
            var post = Instancio.of(Post.class)
                    .ignore(Select.field(Post::getId))
                    .supply(Select.field(Post::getTitle), () -> faker.name().firstName())
                    .supply(Select.field(Post::getContent), () -> faker.name().lastName())
                    .supply(Select.field(Post::getPublished), () -> true)
                    .create();
            repository.save(post);
        }
        var result = mockMvc.perform(get("/api/posts"))
                .andExpect(status().isOk())
                .andReturn();

        // Тело это строка, в этом случае JSON
        var body = result.getResponse().getContentAsString();
        assertThatJson(body).node("content").isArray();

        var posts = repository.findAll();
        var viewPosts = om.writeValueAsString(posts);
        assertThatJson(body).when(Option.IGNORING_ARRAY_ORDER).node("content").isEqualTo(viewPosts);
    }

    @Test
    public void testIndexPublishedOnly() throws Exception {
        // Добавить пользователей
        for (int x = 1; x <= 3; x++) {
            var publish = x % 2 == 0;
            var post = Instancio.of(Post.class)
                    .ignore(Select.field(Post::getId))
                    .supply(Select.field(Post::getTitle), () -> faker.name().firstName())
                    .supply(Select.field(Post::getContent), () -> faker.name().lastName())
                    .supply(Select.field(Post::getPublished), () -> publish)
                    .create();
            repository.save(post);
        }
        var result = mockMvc.perform(get("/api/posts?isPublishedOnly=true"))
                .andExpect(status().isOk())
                .andReturn();

        // Тело это строка, в этом случае JSON
        var body = result.getResponse().getContentAsString();
        assertThatJson(body).node("content").isArray();

        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
        var posts = repository.findByPublishedTrue(pageable);
        var viewPosts = om.writeValueAsString(posts.getContent());
        assertThatJson(body).when(Option.IGNORING_ARRAY_ORDER).node("content").isEqualTo(viewPosts);
    }

    @Test
    public void testShow() throws Exception {
        var post = Instancio.of(Post.class)
                .ignore(Select.field(Post::getId))
                .ignore(Select.field(Post::getCreatedAt))
                .ignore(Select.field(Post::getUpdatedAt))
                .supply(Select.field(Post::getTitle), () -> faker.name().firstName())
                .supply(Select.field(Post::getContent), () -> faker.name().lastName())
                .supply(Select.field(Post::getPublished), () -> true)
                .create();
        repository.save(post);

        var result = mockMvc.perform(get("/api/posts/" + post.getId()))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isObject();

        var viewPost = om.writeValueAsString(repository.findById(post.getId()));
        assertThatJson(body)
                .whenIgnoringPaths("updatedAt", "createdAt")
                .isEqualTo(viewPost);
    }

    @Test
    public void testPutUpdate() throws Exception {
        var post = Instancio.of(Post.class)
                .ignore(Select.field(Post::getId))
                .ignore(Select.field(Post::getCreatedAt))
                .ignore(Select.field(Post::getUpdatedAt))
                .supply(Select.field(Post::getTitle), () -> faker.name().firstName())
                .supply(Select.field(Post::getContent), () -> faker.name().lastName())
                .supply(Select.field(Post::getPublished), () -> true)
                .create();
        repository.save(post);

        var data = new HashMap<>();
        data.put("title", "Mike");
        data.put("content", "email@email.com");

        var request = put("/api/posts/" + post.getId())
                .contentType(MediaType.APPLICATION_JSON)
                // ObjectMapper конвертирует Map в JSON
                .content(om.writeValueAsString(data));

        mockMvc.perform(request)
                .andExpect(status().isOk());

        var postAfterUpdate = repository.findById(post.getId()).get();
        assertEquals("Mike", postAfterUpdate.getTitle());
        assertEquals("email@email.com", postAfterUpdate.getContent());
    }

    @Test
    public void testPatchUpdate() throws Exception {
        var post = Instancio.of(Post.class)
                .ignore(Select.field(Post::getId))
                .ignore(Select.field(Post::getCreatedAt))
                .ignore(Select.field(Post::getUpdatedAt))
                .supply(Select.field(Post::getTitle), () -> faker.name().firstName())
                .supply(Select.field(Post::getContent), () -> faker.name().lastName())
                .supply(Select.field(Post::getPublished), () -> true)
                .create();
        repository.save(post);

        var data = new HashMap<>();
        data.put("content", "email@email.com");

        var request = patch("/api/posts/" + post.getId())
                .contentType(MediaType.APPLICATION_JSON)
                // ObjectMapper конвертирует Map в JSON
                .content(om.writeValueAsString(data));

        mockMvc.perform(request)
                .andExpect(status().isOk());

        var postAfterUpdate = repository.findById(post.getId()).get();
        assertFalse(postAfterUpdate.getTitle() == null);
        assertEquals(post.getTitle(), postAfterUpdate.getTitle());
        assertEquals("email@email.com", postAfterUpdate.getContent());
    }
}

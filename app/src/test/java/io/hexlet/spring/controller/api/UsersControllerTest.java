package io.hexlet.spring.controller.api;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.hexlet.spring.model.User;
import io.hexlet.spring.repository.UserRepository;
import net.datafaker.Faker;
import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;

@SpringBootTest
@AutoConfigureMockMvc
public class UsersControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Faker faker;

    @Autowired
    private UserRepository repository;

    @Autowired
    private ObjectMapper om;

    @BeforeEach
    void clearRepository() {
        repository.deleteAll();
    }

    @Test
    public void testDelete() throws Exception {
        // Добавить пользователей
        Long idUser = 0L;
        for (int x = 1; x <= 3; x++) {
            var user = Instancio.of(User.class)
                    .ignore(Select.field(User::getId))
                    .supply(Select.field(User::getEmail), () -> faker.internet().emailAddress())
                    .supply(Select.field(User::getFirstName), () -> faker.name().firstName())
                    .supply(Select.field(User::getLastName), () -> faker.name().lastName())
                    .create();
            repository.save(user);
            idUser = user.getId();
        }
        var result = mockMvc.perform(delete("/api/users/" + idUser))
                .andExpect(status().isNoContent())
                .andReturn();

        var users = repository.findAll();
        assertEquals(2, users.size());
        Long finalIdUser = idUser;
        assertTrue(users.stream()
                .filter(u -> u.getId() == finalIdUser)
                .count() == 0);
    }

    @Test
    public void testCreate() throws Exception {
        var user = Instancio.of(User.class)
                .ignore(Select.field(User::getId))
                .ignore(Select.field(User::getCreatedAt))
                .ignore(Select.field(User::getUpdatedAt))
                .supply(Select.field(User::getEmail), () -> faker.internet().emailAddress())
                .supply(Select.field(User::getFirstName), () -> faker.name().firstName())
                .supply(Select.field(User::getLastName), () -> faker.name().lastName())
                .create();

        var request = post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                // ObjectMapper конвертирует Map в JSON
                .content(om.writeValueAsString(user));

        var result = mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isObject();

        var viewUser = om.writeValueAsString(user);
        assertThatJson(body)
                .whenIgnoringPaths("updatedAt", "createdAt", "id")
                .isEqualTo(viewUser);
    }

    @Test
    public void testIndex() throws Exception {
        // Добавить пользователей
        for (int x = 1; x <= 3; x++) {
            var user = Instancio.of(User.class)
                    .ignore(Select.field(User::getId))
                    .supply(Select.field(User::getEmail), () -> faker.internet().emailAddress())
                    .supply(Select.field(User::getFirstName), () -> faker.name().firstName())
                    .supply(Select.field(User::getLastName), () -> faker.name().lastName())
                    .create();
            repository.save(user);
        }
        var result = mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andReturn();

        // Тело это строка, в этом случае JSON
        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray();

        var users = repository.findAll();
        var viewUsers = om.writeValueAsString(users);
        assertThatJson(body).isEqualTo(viewUsers);
    }

    @Test
    public void testShow() throws Exception {
        var user = Instancio.of(User.class)
                .ignore(Select.field(User::getId))
                .ignore(Select.field(User::getCreatedAt))
                .ignore(Select.field(User::getUpdatedAt))
                .supply(Select.field(User::getEmail), () -> faker.internet().emailAddress())
                .supply(Select.field(User::getFirstName), () -> faker.name().firstName())
                .supply(Select.field(User::getLastName), () -> faker.name().lastName())
                .create();
        repository.save(user);

        var result = mockMvc.perform(get("/api/users/" + user.getId()))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isObject();

        var viewUser = om.writeValueAsString(repository.findById(user.getId()));
        assertThatJson(body)
                .whenIgnoringPaths("updatedAt", "createdAt")
                .isEqualTo(viewUser);
    }

    @Test
    public void testPutUpdate() throws Exception {
        var user = Instancio.of(User.class)
                .ignore(Select.field(User::getId))
                .ignore(Select.field(User::getCreatedAt))
                .ignore(Select.field(User::getUpdatedAt))
                .supply(Select.field(User::getEmail), () -> faker.internet().emailAddress())
                .supply(Select.field(User::getFirstName), () -> faker.name().firstName())
                .create();
        repository.save(user);

        var data = new HashMap<>();
        data.put("email", "email@email.com");

        var request = put("/api/users/" + user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                // ObjectMapper конвертирует Map в JSON
                .content(om.writeValueAsString(data));

        mockMvc.perform(request)
                .andExpect(status().isOk());

        var userAfterUpdate = repository.findById(user.getId()).get();
        assertEquals("email@email.com", userAfterUpdate.getEmail());
        assertTrue(userAfterUpdate.getFirstName() == null);
    }

    @Test
    public void testPatchUpdate() throws Exception {
        var user = Instancio.of(User.class)
                .ignore(Select.field(User::getId))
                .ignore(Select.field(User::getCreatedAt))
                .ignore(Select.field(User::getUpdatedAt))
                .supply(Select.field(User::getEmail), () -> faker.internet().emailAddress())
                .supply(Select.field(User::getFirstName), () -> faker.name().firstName())
                .create();
        repository.save(user);

        var data = new HashMap<>();
        data.put("email", "email@email.com");

        var request = patch("/api/users/" + user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                // ObjectMapper конвертирует Map в JSON
                .content(om.writeValueAsString(data));

        mockMvc.perform(request)
                .andExpect(status().isOk());

        var userAfterUpdate = repository.findById(user.getId()).get();
        assertEquals("email@email.com", userAfterUpdate.getEmail());
        assertFalse(userAfterUpdate.getFirstName() == null);
    }
}

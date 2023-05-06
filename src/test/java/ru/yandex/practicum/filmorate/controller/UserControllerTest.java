package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    private User userIn;
    private String jsonUser;
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @BeforeEach
    void createUserAndParseInJson() throws JsonProcessingException {
        userIn = new User(null, "asd@mail.ru", "a", "s",
                LocalDate.of(1900, Month.DECEMBER, 8), null);
        jsonUser = mapper.writeValueAsString(userIn);
    }

    @AfterEach
    void cleanHashMap() throws Exception {
        mockMvc.perform(delete("/users"));
    }

    @Test
    void shouldReturnUserAndStatus200WhenCallMethodPost() throws Exception {
        MvcResult result = mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(jsonUser)).andExpect(status().isOk()).andReturn();
        User userOut = mapper.readValue(result.getResponse().getContentAsString(), User.class);
        assertEquals(userIn, userOut);
    }

    @Test
    void shouldReturnListOfUsersWithStatus200WhenCallMethodGet() throws Exception {
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(jsonUser)).andExpect(status().isOk());
        MvcResult result = mockMvc.perform(get("/users")).andExpect(status().isOk()).andReturn();
        List<User> users = mapper.readValue(result.getResponse().getContentAsString(),
                 new TypeReference<List<User>>() {
                });
        User userOut = users.get(0);
        assertEquals(userIn, userOut);
    }

    @Test
    void shouldUpdateUserAndReturnStatus200WhenCallMethodPut() throws Exception {
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(jsonUser)).andExpect(status().isOk());
        User updatedUser = new User(1, "mail@mail.ru", "Login", "Name",
                LocalDate.of(1999, 2, 2), null);
        String updatedUserInJson = mapper.writeValueAsString(updatedUser);
        MvcResult result = mockMvc.perform(put("/users").contentType(MediaType.APPLICATION_JSON)
                        .content(updatedUserInJson))
                .andExpect(status().isOk()).andReturn();
        User userOut = mapper.readValue(result.getResponse().getContentAsString(), User.class);
        assertEquals(updatedUser, userOut);
    }

    @Test
    void shouldReturnStatus400WhenCallMethodPostAndUserDontHasEmail() throws Exception {
        User incorrectUser = new User(null, null, "a", "s",
                LocalDate.of(1900, Month.DECEMBER, 8), null);
        String json = mapper.writeValueAsString(incorrectUser);
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnStatus400WhenCallMethodPostAndUserDontHasLogin() throws Exception {
        User incorrectUser = new User(null, "mail@mail.ru", null, "s",
                LocalDate.of(1900, Month.DECEMBER, 8), null);
        String json = mapper.writeValueAsString(incorrectUser);
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnStatus400WhenCallMethodPostAndUserHasIncorrectEmail() throws Exception {
        User incorrectUser = new User(null, "asasasas", "a", "s",
                LocalDate.of(1900, Month.DECEMBER, 8), null);
        String json = mapper.writeValueAsString(incorrectUser);
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnStatus400WhenCallMethodPostAndUserHasBlankLogin() throws Exception {
        User incorrectUser = new User(null, "mail@mail.ru", "", "s",
                LocalDate.of(1900, Month.DECEMBER, 8), null);
        String json = mapper.writeValueAsString(incorrectUser);
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnStatus400WhenCallMethodPostAndUserHasSpaceInLogin() throws Exception {
        User incorrectUser = new User(null, "mail@mail.ru", "sd ds", "s",
                LocalDate.of(1900, Month.DECEMBER, 8), null);
        String json = mapper.writeValueAsString(incorrectUser);
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnStatus400WhenCallMethodPostAndUserHasBirthdayInFuture() throws Exception {
        User incorrectUser = new User(null, "mail@mail.ru", "Login", "s",
                LocalDate.of(2200, Month.DECEMBER, 8), null);
        String json = mapper.writeValueAsString(incorrectUser);
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnStatus400WhenCallMethodPostAndUserHasBirthdayEqualsNull() throws Exception {
        User incorrectUser = new User(null, "mail@mail.ru", "nulo", "s", null, null);
        String json = mapper.writeValueAsString(incorrectUser);
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isBadRequest());
    }
}
package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserControllerTest {

    private static final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private static User userIn;
    private static String jsonUser;
    @Autowired
    private MockMvc mockMvc;

    @BeforeAll
    static void testCreateUserAndParseInJson() throws JsonProcessingException {
        userIn = new User(null, "asd@mail.ru", "a", "s",
                LocalDate.of(1900, Month.DECEMBER, 8), null);
        jsonUser = mapper.writeValueAsString(userIn);
    }

    private static Stream<User> testShouldReturnStatus400WhenCallMethodPostAndUserIsIncorrect() {
        return Stream.of(
                new User(null, null, "a", "s",
                        LocalDate.of(1900, Month.DECEMBER, 8), null),
                new User(null, "mail@mail.ru", null, "s",
                        LocalDate.of(1900, Month.DECEMBER, 8), null),
                new User(null, "asasasas", "a", "s",
                        LocalDate.of(1900, Month.DECEMBER, 8), null),
                new User(null, "mail@mail.ru", "", "s",
                        LocalDate.of(1900, Month.DECEMBER, 8), null),
                new User(null, "mail@mail.ru", "sd ds", "s",
                        LocalDate.of(1900, Month.DECEMBER, 8), null),
                new User(null, "mail@mail.ru", "Login", "s",
                        LocalDate.of(2200, Month.DECEMBER, 8), null),
                new User(null, "mail@mail.ru", "nulo", "s", null, null)
        );
    }

    @AfterEach
    protected void testCleanHashMap() throws Exception {
        mockMvc.perform(delete("/users"));
    }

    @Test
    protected void testShouldReturnUserAndStatus200WhenCallMethodPost() throws Exception {
        MvcResult result = mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(jsonUser)).andExpect(status().isOk()).andReturn();
        User userOut = mapper.readValue(result.getResponse().getContentAsString(), User.class);
        assertEquals(userIn, userOut, "Возвращенный пользователь методом post не равен добавленному.");
    }

    @Test
    protected void testShouldReturnListOfUsersWithStatus200WhenCallMethodGet() throws Exception {
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(jsonUser)).andExpect(status().isOk());
        MvcResult result = mockMvc.perform(get("/users")).andExpect(status().isOk()).andReturn();
        List<User> users = mapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<List<User>>() {
                });
        User userOut = users.get(0);
        assertEquals(userIn, userOut, "Содержимое возвращенного списка пользователей методом get не равно " +
                "добавленным пользователям.");
    }

    @Test
    protected void testShouldUpdateUserAndReturnStatus200WhenCallMethodPut() throws Exception {
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(jsonUser)).andExpect(status().isOk());
        User updatedUser = new User(1, "mail@mail.ru", "Login", "Name",
                LocalDate.of(1999, 2, 2), null);
        String updatedUserInJson = mapper.writeValueAsString(updatedUser);
        MvcResult result = mockMvc.perform(put("/users").contentType(MediaType.APPLICATION_JSON)
                .content(updatedUserInJson)).andExpect(status().isOk()).andReturn();
        User userOut = mapper.readValue(result.getResponse().getContentAsString(), User.class);
        assertEquals(updatedUser, userOut, "Возвращенный пользователь в результате метода post не равен" +
                " ему же до добавления.");
    }

    @ParameterizedTest
    @MethodSource
    protected void testShouldReturnStatus400WhenCallMethodPostAndUserIsIncorrect(User incorrectUser) throws Exception {
        String json = mapper.writeValueAsString(incorrectUser);
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isBadRequest());
    }

    @Test
    protected void testShouldReturnStatus200AndDeleteFromStorageUserByIdWhenCallMethodDelete() throws Exception {
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(jsonUser)).andExpect(status().isOk());
        mockMvc.perform(delete("/users/1")).andExpect(status().isOk());
        mockMvc.perform(get("/users/1")).andExpect(status().isNotFound());
    }

    @Test
    protected void testShouldReturnStatus200WhenAddUserToFriendsAndDeleteUserFromFriends() throws Exception {
        User userIn2 = new User(null, "aasd@mail.ru", "a", "s",
                LocalDate.of(1900, Month.DECEMBER, 8), null);
        String jsonUser2 = mapper.writeValueAsString(userIn2);
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(jsonUser)).andExpect(status().isOk()).andReturn();
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(jsonUser2)).andExpect(status().isOk()).andReturn();

        mockMvc.perform(put("/users/1/friends/2")).andExpect(status().isOk());

        MvcResult result = mockMvc.perform(get("/users/1/friends")).andExpect(status().isOk()).andReturn();
        List<User> friendsOfUser = mapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<List<User>>() {
                });
        assertEquals(1, friendsOfUser.size(), "Список друзей пользователя не равен 1 после добавления" +
                " 1 друга.");

        mockMvc.perform(delete("/users/1/friends/2")).andExpect(status().isOk());
        MvcResult result2 = mockMvc.perform(get("/users/1/friends")).andExpect(status().isOk()).andReturn();
        List<User> friendsOfUser2 = mapper.readValue(result2.getResponse().getContentAsString(),
                new TypeReference<List<User>>() {
                });
        assertEquals(0, friendsOfUser2.size(), "Список друзей пользователя не равен 0 после " +
                "удаления из друзей единственного друга.");
    }

    @Test
    protected void testShouldReturnStatus200AndListOfFriendsOfUserWhenCallMethodGet() throws Exception {
        User userIn2 = new User(null, "fasd@mail.ru", "a", "s",
                LocalDate.of(1900, Month.DECEMBER, 8), null);
        String jsonUser2 = mapper.writeValueAsString(userIn2);
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(jsonUser)).andExpect(status().isOk()).andReturn();
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(jsonUser2)).andExpect(status().isOk()).andReturn();

        mockMvc.perform(put("/users/1/friends/2")).andExpect(status().isOk());

        MvcResult result = mockMvc.perform(get("/users/1/friends")).andExpect(status().isOk()).andReturn();
        List<User> friendsOfUser = mapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<List<User>>() {
                });
        assertEquals(2, friendsOfUser.get(0).getId(), "Список друзей пользователя не содержит " +
                "пользователя с id 2 после добавления его в друзья.");
    }
}
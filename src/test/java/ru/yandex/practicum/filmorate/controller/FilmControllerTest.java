package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
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
import ru.yandex.practicum.filmorate.model.Film;
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
class FilmControllerTest {

    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    @Autowired
    private MockMvc mockMvc;

    private static Stream<Film> testShouldReturnStatus400WhenCallMethodPostAndFilmIsIncorrect() {
        return Stream.of(new Film(null, null, "description",
                        LocalDate.of(2000, 5, 4), 120, 5),
                new Film(null, "  ", "description",
                        LocalDate.of(2000, 5, 4), 120, 4),
                new Film(null, "Name", null,
                        LocalDate.of(2000, 5, 4), 120, 3),
                new Film(null, "Name", "   ",
                        LocalDate.of(2000, 5, 4), 120, 2),
                new Film(null, "Name", "123456789101111111120222222223033333333404444444450" +
                        "55555555606666666670777777778088888888909999999910011111111111111111" +
                        "123456789101111111120222222223033333333404444444450" +
                        "55555555606666666670777777778088888888909999999910011111111111111111",
                        LocalDate.of(2000, 5, 4), 120, 2),
                new Film(null, "Name", "description",
                        LocalDate.of(1800, 5, 4), 120, 3),
                new Film(null, "Name", "description",
                        LocalDate.of(2000, 5, 4), 0, 4),
                new Film(null, "Name", "description",
                        LocalDate.of(2000, 5, 4), -120, 5)
        );
    }

    @Test
    protected void testShouldReturnStatus200WhenCallMethodPost() throws Exception {
        Film film = new Film(null, "Name", "description",
                LocalDate.of(2000, 5, 4), 120, 5);
        String json = mapper.writeValueAsString(film);
        MvcResult result = mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isOk()).andReturn();
        Film filmOut = mapper.readValue(result.getResponse().getContentAsString(), Film.class);
        assertEquals(film, filmOut, "Возвращенный фильм в результате метода post не равен переданному.");
    }

    @Test
    protected void testShouldReturnStatus200WhenCallMethodGet() throws Exception {
        Film film = new Film(null, "Name", "description",
                LocalDate.of(2000, 5, 4), 120, 2);
        String json = mapper.writeValueAsString(film);
        mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isOk());
        MvcResult result = mockMvc.perform(get("/films")).andExpect(status().isOk()).andReturn();
        List<Film> films = mapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<List<Film>>() {
                });
        Film filmOut = films.get(0);
        assertEquals(film, filmOut, "Содержимое возвращенного списка фильмов в результате метода get " +
                "не равно заранее добавленным фильмам методом post.");
    }

    @Test
    protected void testShouldReturnStatus200WhenCallMethodPut() throws Exception {
        Film film = new Film(null, "Name", "description",
                LocalDate.of(2000, 5, 4), 120, 3);
        String json = mapper.writeValueAsString(film);
        mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isOk());
        Film updatedFilm = new Film(1, "asds", "sadad",
                LocalDate.of(1999, 6, 12), 100, 4);
        String updatedFilmInJson = mapper.writeValueAsString(updatedFilm);
        MvcResult result = mockMvc.perform(put("/films").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(updatedFilmInJson)).andExpect(status().isOk()).andReturn();
        Film filmOut = mapper.readValue(result.getResponse().getContentAsString(), Film.class);
        assertEquals(updatedFilm, filmOut, "Возвращенный фильм не равен в результате метода put не равен" +
                "ему же до отправления.");
    }

    @Test
    protected void testShouldReturnStatus200AndGetFilmByIdWhenCallMethodGet() throws Exception {
        Film film = new Film(null, "Name", "description",
                LocalDate.of(2000, 5, 4), 120, 3);
        String json = mapper.writeValueAsString(film);
        mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isOk());
        MvcResult result = mockMvc.perform(get("/films/1")).andExpect(status().isOk()).andReturn();
        Film returnedFilm = mapper.readValue(result.getResponse().getContentAsString(), Film.class);
        assertEquals(film, returnedFilm, "Возвращенный фильм в результате метода get " +
                "не равен ему же заранее добавленному методом post.");
    }

    @Test
    protected void testShouldReturnStatus200AndDeleteFromStorageFilmByIdWhenCallMethodDelete() throws Exception {
        Film film = new Film(null, "Name", "description",
                LocalDate.of(2000, 5, 4), 120, 3);
        String json = mapper.writeValueAsString(film);
        mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isOk());
        mockMvc.perform(delete("/films/1")).andExpect(status().isOk());
        mockMvc.perform(get("/films/1")).andExpect(status().isNotFound());
    }

    @Test
    protected void testShouldReturnStatus200WhenAddLikeToFilmAndDeleteLikeFromFilm() throws Exception {
        Film film = new Film(null, "Name", "description",
                LocalDate.of(2000, 5, 4), 120, 3);
        String json = mapper.writeValueAsString(film);
        mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isOk());

        User userIn = new User(null, "asd@mail.ru", "a", "s",
                LocalDate.of(1900, Month.DECEMBER, 8), null);
        String jsonUser = mapper.writeValueAsString(userIn);
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(jsonUser)).andExpect(status().isOk()).andReturn();

        mockMvc.perform(put("/films/1/like/1")).andExpect(status().isOk());

        MvcResult result = mockMvc.perform(get("/films/1")).andExpect(status().isOk()).andReturn();
        Film returnedFilm = mapper.readValue(result.getResponse().getContentAsString(), Film.class);
        assertEquals(1, returnedFilm.getLikes().size(), "Возвращенный фильм не имеет заранее" +
                " поставленного лайка.");

        mockMvc.perform(delete("/films/1/like/1")).andExpect(status().isOk());
        MvcResult result2 = mockMvc.perform(get("/films/1")).andExpect(status().isOk()).andReturn();
        Film returnedFilm2 = mapper.readValue(result2.getResponse().getContentAsString(), Film.class);
        assertEquals(0, returnedFilm2.getLikes().size(), "Возвращенный фильм имеет не 0 лайков.");
    }

    @ParameterizedTest
    @MethodSource
    protected void testShouldReturnStatus400WhenCallMethodPostAndFilmIsIncorrect(Film incorrectFilm) throws Exception {
        String json = mapper.writeValueAsString(incorrectFilm);
        mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isBadRequest());

        Film film2 = new Film(null, "  ", "description",
                LocalDate.of(2000, 5, 4), 120, 1);
        String json2 = mapper.writeValueAsString(film2);
        mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(json2)).andExpect(status().isBadRequest());
    }
}
package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = FilmController.class)
class FilmControllerTest {

    @Autowired
    private MockMvc mockMvc;
    private ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @AfterEach
    void deleteAllFilms() throws Exception {
        mockMvc.perform(delete("/films"));
    }

    @Test
    void shouldReturnStatus200WhenCallMethodPost() throws Exception {
        Film film = new Film(null, "Name", "description"
                , LocalDate.of(2000, 5, 4), 120);
        String json = mapper.writeValueAsString(film);
        MvcResult result = mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isOk()).andReturn();
        Film filmOut = mapper.readValue(result.getResponse().getContentAsString(), Film.class);
        assertEquals(film, filmOut);
    }

    @Test
    void shouldReturnStatus200WhenCallMethodGet() throws Exception {
        Film film = new Film(null, "Name", "description"
                , LocalDate.of(2000, 5, 4), 120);
        String json = mapper.writeValueAsString(film);
        mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isOk());
        MvcResult result = mockMvc.perform(get("/films")).andExpect(status().isOk()).andReturn();
        List<Film> films = mapper.readValue(result.getResponse().getContentAsString()
                , new TypeReference<List<Film>>(){});
        Film filmOut = films.get(0);
        assertEquals(film, filmOut);
    }

    @Test
    void shouldReturnStatus200WhenCallMethodPut() throws Exception {
        Film film = new Film(null, "Name", "description"
                , LocalDate.of(2000, 5, 4), 120);
        String json = mapper.writeValueAsString(film);
        mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isOk());
        Film updatedFilm = new Film(1, "asds", "sadad"
                , LocalDate.of(1999, 6, 12), 100);
        String updatedFilmInJson = mapper.writeValueAsString(updatedFilm);
        MvcResult result = mockMvc.perform(put("/films").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(updatedFilmInJson)).andExpect(status().isOk()).andReturn();
        Film filmOut = mapper.readValue(result.getResponse().getContentAsString(), Film.class);
        assertEquals(updatedFilm, filmOut);
    }

    @Test
    void shouldReturnStatus400WhenCallMethodPostAndNameEqualsNullOrIsBlank() throws Exception {
        Film film = new Film(null, null, "description"
                , LocalDate.of(2000, 5, 4), 120);
        String json = mapper.writeValueAsString(film);
        mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isBadRequest());

        Film film2 = new Film(null, "  ", "description"
                , LocalDate.of(2000, 5, 4), 120);
        String json2 = mapper.writeValueAsString(film2);
        mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(json2)).andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnStatus400WhenCallMethodPostAndDescriptionEqualsNullOrIsBlankOrLengthIsMoreThen200()
            throws Exception {
        Film film = new Film(null, "Name", null
                , LocalDate.of(2000, 5, 4), 120);
        String json = mapper.writeValueAsString(film);
        mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isBadRequest());

        Film film2 = new Film(null, "Name", "   "
                , LocalDate.of(2000, 5, 4), 120);
        String json2 = mapper.writeValueAsString(film2);
        mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(json2)).andExpect(status().isBadRequest());

        Film film3 = new Film(null, "Name", "123456789101111111120222222223033333333404444444450" +
                "55555555606666666670777777778088888888909999999910011111111111111111" +
                "123456789101111111120222222223033333333404444444450" +
                "55555555606666666670777777778088888888909999999910011111111111111111"
                , LocalDate.of(2000, 5, 4), 120);
        String json3 = mapper.writeValueAsString(film3);
        mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(json3)).andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnStatus400WhenCallMethodPostAndReleaseDateIsEarlierThan28_12_1895() throws Exception {
        Film film = new Film(null, "Name", "description"
                , LocalDate.of(1800, 5, 4), 120);
        String json = mapper.writeValueAsString(film);
        mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnStatus400WhenCallMethodPostAndDurationEquals0OrNegative() throws Exception {
        Film film = new Film(null, "Name", "description"
                , LocalDate.of(2000, 5, 4), 0);
        String json = mapper.writeValueAsString(film);
        mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isBadRequest());

        Film film2 = new Film(null, "Name", "description"
                , LocalDate.of(2000, 5, 4), -120);
        String json2 = mapper.writeValueAsString(film2);
        mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(json2)).andExpect(status().isBadRequest());
    }
}
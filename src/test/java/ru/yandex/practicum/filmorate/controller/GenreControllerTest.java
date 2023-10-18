package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class GenreControllerTest {

    private final ObjectMapper mapper = new ObjectMapper();
    @Autowired
    private MockMvc mockMvc;

    @Test
    protected void testShouldReturnStatus200AndGenreWhenCallMethodGet() throws Exception {
        MvcResult result = mockMvc.perform(get("/genres/1")).andExpect(status().isOk()).andReturn();
        Genre returnedGenre = mapper.readValue(result.getResponse().getContentAsString(), Genre.class);
        assertEquals(Genre.COMEDY, returnedGenre, "Возвращенный жанр в результате метода get " +
                "не соответствует запрошенному.");
    }

    @Test
    protected void testShouldReturnStatus200AndListOfGenreMpaWhenCallMethodGet() throws Exception {
        MvcResult result = mockMvc.perform(get("/genres")).andExpect(status().isOk()).andReturn();
        List<Genre> returnedGenres = mapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<List<Genre>>() {
                });
        assertEquals(6, returnedGenres.size(), "Размер возвращенного списка жанров не равен 6.");
    }
}
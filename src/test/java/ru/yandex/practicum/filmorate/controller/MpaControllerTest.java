package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.yandex.practicum.filmorate.model.RatingMpa;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class MpaControllerTest {

    private final ObjectMapper mapper = new ObjectMapper();
    @Autowired
    private MockMvc mockMvc;

    @Test
    protected void testShouldReturnStatus200AndRatingMpaWhenCallMethodGet() throws Exception {
        MvcResult result = mockMvc.perform(get("/mpa/1")).andExpect(status().isOk()).andReturn();
        RatingMpa returnedRating = mapper.readValue(result.getResponse().getContentAsString(), RatingMpa.class);
        assertEquals(RatingMpa.G, returnedRating, "Возвращенный рейтинг не равен запрошенному методом get.");
    }

    @Test
    protected void testShouldReturnStatus200AndListOfRatingMpaWhenCallMethodGet() throws Exception {
        MvcResult result = mockMvc.perform(get("/mpa")).andExpect(status().isOk()).andReturn();
        List<RatingMpa> returnedRatings = mapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<List<RatingMpa>>() {
                });
        assertEquals(5, returnedRatings.size(), "Размер возвращенного списка рейтингов не равен 5.");
    }
}
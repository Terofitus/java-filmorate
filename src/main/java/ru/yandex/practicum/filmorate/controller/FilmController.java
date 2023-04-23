package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final HashMap<Integer, Film> films = new HashMap<>();
    private Integer generatedId = 1;

    @GetMapping
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public Film createFilm(@RequestBody @Valid Film film) {
        if (films.containsValue(film)) {
            log.error("Фильм {} {} года уже добавлен", film.getName(), film.getReleaseDate().getYear());
            throw new ValidationException("Данный фильм уже добавлен");
        }
        film.setId(generatedId++);
        films.put(film.getId(), film);
        log.info("Добавлен фильм {} {} года", film.getName(), film.getReleaseDate().getYear());
        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody @Valid Film film) {
        if (!films.containsKey(film.getId())) {
            log.error("Фильм {} не может быть обновлен, так как не был добавлен", film.getName());
            throw new ValidationException("Фильм не может быть обновлен, так как не был добавлен");
        }
        films.put(film.getId(), film);
        log.info("Фильм с id {} был обновлен", film.getId());
        return film;
    }

    @DeleteMapping
    public void deleteAllFilms() {
        films.clear();
        generatedId = 1;
        log.info("Все фильмы удалены");
    }
}

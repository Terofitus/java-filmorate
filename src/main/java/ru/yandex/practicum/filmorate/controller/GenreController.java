package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.LinkedHashSet;
import java.util.Set;

@RestController
public class GenreController {

    @GetMapping("/genres")
    public Set<Genre> getAllGenres() {
        Set<Genre> genres = new LinkedHashSet<>();
        for (int i = 1; i <= Genre.values().length; i++) {
            genres.add(Genre.forValues(i));
        }
        return genres;
    }

    @GetMapping("/genres/{id}")
    public Genre getGenre(@PathVariable Integer id) {
        switch (id) {
            case 1:
                return Genre.COMEDY;
            case 2:
                return Genre.DRAMA;
            case 3:
                return Genre.CARTOON;
            case 4:
                return Genre.THRILLER;
            case 5:
                return Genre.DOCUMENTARY;
            case 6:
                return Genre.ACTION;
            default:
                throw new ObjectNotFoundException("Запрошен жанр с несуществующим id.");
        }
    }
}

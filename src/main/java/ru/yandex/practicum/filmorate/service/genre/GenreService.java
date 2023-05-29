package ru.yandex.practicum.filmorate.service.genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Set;

public interface GenreService {
    Set<Genre> getAllGenres();

    Genre getGenre(Integer id);
}

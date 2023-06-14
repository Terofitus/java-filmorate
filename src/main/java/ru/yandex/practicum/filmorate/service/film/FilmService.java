package ru.yandex.practicum.filmorate.service.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmService {
    List<Film> getAllFilms();

    Film addFilmToStorage(Film film);

    Film updateFilmInStorage(Film film);

    void deleteFilmFromStorageById(Integer id);

    void deleteAllFilms();

    Film getFilmById(Integer id);

    void addLikeToFilm(Integer idOfUser, Integer idOfFilm);

    void deleteLikeFromFilm(Integer idOfUser, Integer idOfFilm);

    List<Film> getMostPopularFilms(Integer count);
}

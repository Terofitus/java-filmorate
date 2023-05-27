package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    Film getFilmById(Integer id);

    List<Film> getAllFilms();

    Film addFilmToStorage(Film film);

    Film updateFilmInStorage(Film film);

    void deleteFilmFromStorageById(Integer id);

    void deleteAllFilms();

    void addLikeToFilm(Integer idOfUser, Integer idOfFilm);

    void deleteLikeFromFilm(Integer idOfUser, Integer idOfFilm);
}

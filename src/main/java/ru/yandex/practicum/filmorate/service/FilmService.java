package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film addFilmToStorage(Film film) {
        return filmStorage.addFilmToStorage(film);
    }

    public Film updateFilmInStorage(Film film) {
        return filmStorage.updateFilmInStorage(film);
    }

    public void deleteFilmFromStorageById(Integer id) {
        filmStorage.deleteFilmFromStorageById(id);
    }

    public void deleteAllFilms() {
        filmStorage.deleteAllFilms();
    }

    public Film getFilmById(Integer id) {
        return filmStorage.getFilmById(id);
    }

    public void addLikeToFilm(Integer idOfUser, Integer idOfFilm) {
        User user = userStorage.getUserById(idOfUser);
        filmStorage.getFilmById(idOfFilm).getLikes().add(user.getId());
    }

    public void deleteLikeFromFilm(Integer idOfUser, Integer idOfFilm) {
        User user = userStorage.getUserById(idOfUser);
        filmStorage.getFilmById(idOfFilm).getLikes().remove(user.getId());
    }

    public List<Film> getMostPopularFilms(Integer count) {
        return filmStorage.getAllFilms().stream().sorted((o1, o2) -> (o1.getLikes().size() - o2.getLikes().size()) * -1)
                .limit(count).collect(Collectors.toList());
    }
}

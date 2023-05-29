package ru.yandex.practicum.filmorate.service.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmServiceImpl implements FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmServiceImpl(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                           @Qualifier("userDbStorage") UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    @Override
    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    @Override
    public Film addFilmToStorage(Film film) {
        return filmStorage.addFilmToStorage(film);
    }

    @Override
    public Film updateFilmInStorage(Film film) {
        return filmStorage.updateFilmInStorage(film);
    }

    @Override
    public void deleteFilmFromStorageById(Integer id) {
        filmStorage.deleteFilmFromStorageById(id);
    }

    @Override
    public void deleteAllFilms() {
        filmStorage.deleteAllFilms();
    }

    @Override
    public Film getFilmById(Integer id) {
        return filmStorage.getFilmById(id);
    }

    @Override
    public void addLikeToFilm(Integer idOfUser, Integer idOfFilm) {
        User user = userStorage.getUserById(idOfUser);
        filmStorage.addLikeToFilm(user.getId(), idOfFilm);
    }

    @Override
    public void deleteLikeFromFilm(Integer idOfUser, Integer idOfFilm) {
        User user = userStorage.getUserById(idOfUser);
        filmStorage.deleteLikeFromFilm(user.getId(), idOfFilm);
    }

    @Override
    public List<Film> getMostPopularFilms(Integer count) {
        List<Film> films = filmStorage.getAllFilms();
        return films.stream().sorted(Comparator.comparingInt((Film o) -> o.getLikes().size())
                .thenComparingInt(Film::getId)).limit(count).collect(Collectors.toList());
    }
}

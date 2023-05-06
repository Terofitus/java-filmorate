package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;
import java.util.List;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final HashMap<Integer, Film> films = new HashMap<>();
    private Integer generatedId = 1;

    @Override
    public List<Film> getAllFilms() {
        return List.copyOf(films.values());
    }

    @Override
    public Film getFilmById(Integer id) {
        if (!films.containsKey(id)) {
            log.error("Попытка получить фильм по id, который не был добавлен");
            throw new ObjectNotFoundException("Нет фильма с таким id");
        }
        return films.get(id);
    }

    @Override
    public Film addFilmToStorage(Film film) {
        if (films.containsValue(film)) {
            log.error("Фильм {} {} года уже добавлен", film.getName(), film.getReleaseDate().getYear());
            throw new ValidationException("Данный фильм уже добавлен");
        } else {
            film.setId(generatedId++);
            films.put(film.getId(), film);
            log.info("Добавлен фильм {} {} года", film.getName(), film.getReleaseDate().getYear());
            return film;
        }
    }

    @Override
    public Film updateFilmInStorage(Film film) {
        if (!films.containsKey(film.getId())) {
            log.error("Фильм {} не может быть обновлен, так как не был добавлен", film.getName());
            throw new ObjectNotFoundException("Фильм не может быть обновлен, так как не был добавлен");
        } else {
            films.put(film.getId(), film);
            log.info("Фильм с id {} был обновлен", film.getId());
            return film;
        }
    }

    @Override
    public void deleteFilmFromStorageById(Integer id) {
        if (!films.containsKey(id)) {
            log.error("Попытка удалить фильм по id, который не был добавлен");
            throw new ObjectNotFoundException("Нет фильма с таким id");
        }
        films.remove(id);
    }

    @Override
    public void deleteAllFilms() {
        films.clear();
        generatedId = 1;
        log.info("Все фильмы удалены");
    }
}

package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.RatingMpa;

import java.sql.*;
import java.sql.Date;
import java.util.*;

@Slf4j
@Repository("filmDbStorage")
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film getFilmById(Integer id) {
        Film film = gettingFilmById(id);
        if (film == null) {
            log.error("Попытка получить фильм по недобавленому id {}", id);
            throw new ObjectNotFoundException(String.format("Фильм с id %d не был добавлен.", id));
        }
        addGenresAndLikesToFilm(film);
        log.info("Запрошен фильм по id = {}", id);
        return film;
    }

    @Override
    public List<Film> getAllFilms() {
        String sqlQuery = "SELECT f.id, f.name, f.description, f.release_date, f.duration, r.id AS rating_id" +
                " FROM films AS f LEFT JOIN rating_mpa AS r ON f.rating_id = r.id";
        log.info("Запрошенны все фильмы.");
        List<Film> films = jdbcTemplate.query(sqlQuery, this::makeFilm);
        films.forEach(this::addGenresAndLikesToFilm);
        return films;
    }

    @Override
    public Film addFilmToStorage(Film film) {
        String sqlQuery2 = "INSERT INTO films (name, description, release_date, duration, rating_id) " +
                "VALUES (?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        PreparedStatementCreator connection = con -> {
            PreparedStatement stmt = con.prepareStatement(sqlQuery2, new String[]{"id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());
            stmt.setInt(5, film.getRating().getId());
            return stmt;

        };
        jdbcTemplate.update(connection, keyHolder);
        log.info("Был добавлен фильм с названием {} {} датой выпуска.", film.getName(), film.getReleaseDate());
        film.setId(keyHolder.getKey().intValue());
        if (film.getGenres() != null && film.getGenres().size() != 0) {
            createFilmGenres(film.getGenres(), film.getId());
        }
        return film;
    }

    @Override
    public Film updateFilmInStorage(Film film) {
        if (gettingFilmById(film.getId()) == null) {
            log.error("Попытка обновления не добавленного фильма с id {}.", film.getId());
            throw new ObjectNotFoundException(String.format("Фильм %s с id %d не был добавлен.", film.getName(),
                    film.getId()));
        }

        String sqlQuery = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?," +
                " rating_id = ? WHERE id = ?";
        jdbcTemplate.update(sqlQuery, film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration(), film.getRating().getId(), film.getId());
        if (film.getGenres() != null) {
            try {
                jdbcTemplate.update("DELETE FROM film_genre WHERE film_id = ?", film.getId());
            } catch (Exception e) {
                log.error("Произошла ошибка во время удаления записей жанров фильма с id = {}", film.getId());
            }
            if (film.getGenres().size() != 0) {
                createFilmGenres(film.getGenres(), film.getId());
            }
        }
        log.info("Обновлен фильм с id {}.", film.getId());
        return film;
    }

    @Override
    public void deleteFilmFromStorageById(Integer id) {
        Film filmFromBd = gettingFilmById(id);
        if (filmFromBd == null) {
            log.error("Попытка удаления не добавленного фильма с id {}.", id);
            throw new ObjectNotFoundException(String.format("Фильм с id %d не был добавлен.", id));
        }
        jdbcTemplate.update("DELETE FROM films WHERE id = ?", id);
        log.info("Фильм с id {} был удален.", id);
    }

    @Override
    public void deleteAllFilms() {
        jdbcTemplate.update("DELETE FROM films");
        log.info("Были удаленны все фильмы.");
    }

    @Override
    public void addLikeToFilm(Integer idOfUser, Integer idOfFilm) {
        Film filmFromBd = gettingFilmById(idOfFilm);
        if (filmFromBd == null) {
            log.error("Попытка поставить лайк недобавленному фильму с id {}.", idOfFilm);
            throw new ObjectNotFoundException(String.format("Фильм с id %d не был добавлен.", idOfFilm));
        }
        String sqlQuery = "INSERT INTO film_likes (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sqlQuery, idOfUser, idOfFilm);
    }

    @Override
    public void deleteLikeFromFilm(Integer idOfUser, Integer idOfFilm) {
        Film filmFromBd = gettingFilmById(idOfFilm);
        if (filmFromBd == null) {
            log.error("Попытка удалить лайк с недобавленного фильма с id {}.", idOfFilm);
            throw new ObjectNotFoundException(String.format("Фильм с id %d не был добавлен.", idOfFilm));
        }
        String sqlQuery = "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sqlQuery, idOfFilm, idOfUser);
    }

    private Film makeFilm(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = Film.builder().id(resultSet.getInt("id")).name(resultSet.getString("name"))
            .description(resultSet.getString("description"))
            .releaseDate(resultSet.getDate("release_date").toLocalDate())
            .duration(resultSet.getInt("duration")).build();

        switch (resultSet.getInt("rating_id")) {
            case 1:
                film.setRating(RatingMpa.G);
                break;
            case 2:
                film.setRating(RatingMpa.PG);
                break;
            case 3:
                film.setRating(RatingMpa.PG13);
                break;
            case 4:
                film.setRating(RatingMpa.R);
                break;
            case 5:
                film.setRating(RatingMpa.NC17);
                break;
            default:
                throw new ObjectNotFoundException("Запрошен рейтинг с несуществующим id.");
        }
        return film;
    }

    private void addGenresAndLikesToFilm(Film film) {
        List<Genre> genres = new ArrayList<>();
        try {
            genres = jdbcTemplate.query("SELECT genre_id FROM film_genre WHERE film_id = ?",
                    FilmDbStorage::makeGenre, film.getId());
        } catch (Exception e) {
            log.error("Ошибка вовремя добавления жанров к фильму с id = {}.", film.getId());
        }
        film.setGenres(new LinkedHashSet<>(genres));
        List<Integer> likes = new ArrayList<>();
        try {
            likes = jdbcTemplate.query("SELECT user_id FROM film_likes WHERE film_id = ?",
                    FilmDbStorage::makeLike, film.getId());
        } catch (Exception e) {
            log.error("Ошибка во время попытки добавления лайков к фильму с id = {}.", film.getId());
        }
        film.setLikes(new HashSet<>(likes));
    }

    private static Integer makeLike(ResultSet resultSet, int rowNum) throws SQLException {
        return resultSet.getInt("user_id");
    }

    private static Genre makeGenre(ResultSet resultSet, int rowNum) throws SQLException {
        switch (resultSet.getInt("genre_id")) {
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
                throw new ObjectNotFoundException("Запрошен рейтинг с несуществующим id.");
        }
    }

    private Film gettingFilmById(int id) {
        try {
            String sqlQuery = "SELECT f.id, f.name, f.description, f.release_date, f.duration, r.id AS rating_id" +
                    " FROM films AS f LEFT JOIN rating_mpa AS r ON f.rating_id = r.id WHERE f.id = ?";
            return jdbcTemplate.queryForObject(sqlQuery, this::makeFilm, id);
        } catch (DataAccessException e) {
            return null;
        }
    }

    private void createFilmGenres(Set<Genre> genres, Integer film_id) {
        try {
            jdbcTemplate.batchUpdate("INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)",
                    genres, 100, (PreparedStatement ps, Genre genre) -> {
                        ps.setInt(1, film_id);
                        ps.setInt(2, genre.getId());
                    });
        } catch (Exception e) {
            log.error("Произошла ошибка во время добавления жанров.");
        }
    }
}

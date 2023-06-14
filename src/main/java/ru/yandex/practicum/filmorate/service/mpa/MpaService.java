package ru.yandex.practicum.filmorate.service.mpa;

import ru.yandex.practicum.filmorate.model.RatingMpa;

import java.util.Set;

public interface MpaService {
    Set<RatingMpa> getAllRatings();

    RatingMpa getRatingMpa(Integer id);
}

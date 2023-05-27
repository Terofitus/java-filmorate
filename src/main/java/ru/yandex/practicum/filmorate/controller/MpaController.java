package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.RatingMpa;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@RestController
public class MpaController {

    @GetMapping("/mpa")
    public Set<RatingMpa> getAllRatings() {
        return new LinkedHashSet<>(List.of(RatingMpa.values()));
    }

    @GetMapping("/mpa/{id}")
    public RatingMpa getRatingMpa(@PathVariable Integer id) {
        switch (id) {
            case 1:
                return RatingMpa.G;
            case 2:
                return RatingMpa.PG;
            case 3:
                return RatingMpa.PG13;
            case 4:
                return RatingMpa.R;
            case 5:
                return RatingMpa.NC17;
            default:
                throw new ObjectNotFoundException("Запрошен рейтинг с несуществующим id.");
        }
    }
}

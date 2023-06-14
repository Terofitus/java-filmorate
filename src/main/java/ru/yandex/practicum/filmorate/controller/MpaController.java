package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.RatingMpa;
import ru.yandex.practicum.filmorate.service.mpa.MpaService;

import java.util.Set;

@RestController
public class MpaController {

    private final MpaService mpaService;

    @Autowired
    public MpaController(MpaService mpaService) {
        this.mpaService = mpaService;
    }

    @GetMapping("/mpa")
    public Set<RatingMpa> getAllRatings() {
        return mpaService.getAllRatings();
    }

    @GetMapping("/mpa/{id}")
    public RatingMpa getRatingMpa(@PathVariable Integer id) {
        return mpaService.getRatingMpa(id);
    }
}

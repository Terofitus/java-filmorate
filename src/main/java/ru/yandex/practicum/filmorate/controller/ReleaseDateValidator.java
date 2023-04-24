package ru.yandex.practicum.filmorate.controller;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.Month;

public class ReleaseDateValidator implements ConstraintValidator<ReleaseDate, LocalDate> {
    @Override
    public boolean isValid(LocalDate localDate, ConstraintValidatorContext constraintValidatorContext) {
        return localDate.isAfter(LocalDate.of(1895, Month.DECEMBER, 28));
    }
}

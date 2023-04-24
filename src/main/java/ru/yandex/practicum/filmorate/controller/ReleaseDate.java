package ru.yandex.practicum.filmorate.controller;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ReleaseDateValidator.class)
@Documented
public @interface ReleaseDate {
    String message() default "{ReleaseDate.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

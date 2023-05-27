package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import ru.yandex.practicum.filmorate.controller.ReleaseDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.LinkedHashSet;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Film {
    @EqualsAndHashCode.Exclude
    private Integer id;
    @NotEmpty
    @NotBlank
    private String name;
    @NotEmpty
    @NotBlank
    @Size(max = 200)
    private String description;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ReleaseDate(message = "Дата выпуска фильма не может быть раньше 28.12.1895")
    private LocalDate releaseDate;
    @Positive
    private Integer duration;
    @EqualsAndHashCode.Exclude
    private HashSet<Integer> likes = new HashSet<>();
    @JsonProperty("mpa")
    @EqualsAndHashCode.Exclude
    private RatingMpa rating;
    @EqualsAndHashCode.Exclude
    private LinkedHashSet<Genre> genres;


    public Film(Integer id, String name, String description, LocalDate releaseDate, Integer duration, Integer ratingId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        rating = RatingMpa.forValues(ratingId);
    }
}

package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import ru.yandex.practicum.filmorate.controller.ReleaseDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;

@Data
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

    public Film(Integer id, String name, String description, LocalDate releaseDate, Integer duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }
}

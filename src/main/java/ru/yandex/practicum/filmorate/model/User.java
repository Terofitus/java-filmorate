package ru.yandex.practicum.filmorate.model;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @EqualsAndHashCode.Exclude
    private Integer id;
    @NotEmpty
    @Email
    private String email;
    @Pattern(regexp = "^\\S*", message = "Логин не может содержать пробелы")
    @NotNull(message = "Логин не может быть равен null")
    @NotBlank(message = "Логин не может быть пустым")
    private String login;
    @EqualsAndHashCode.Exclude
    private String name;
    @EqualsAndHashCode.Exclude
    @NotNull
    @PastOrPresent
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;
    @EqualsAndHashCode.Exclude
    private Set<Integer> friends = new HashSet<>();
}

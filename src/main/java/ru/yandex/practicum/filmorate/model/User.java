package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;

@Data
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
    private HashSet<Integer> friends = new HashSet<>();

    public User(Integer id, String email, String login, String name, LocalDate birthday, HashSet<Integer> friends) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
        this.friends = friends;
    }

    public User() {
    }
}

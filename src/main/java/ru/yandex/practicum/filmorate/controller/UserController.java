package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final HashMap<Integer, User> users = new HashMap<>();
    private Integer generatedId = 1;

    @GetMapping
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User createUser(@RequestBody @Valid User user) {
        if(users.values().stream().anyMatch(u -> u.getEmail().equals(user.getEmail()))) {
            log.error("Попытка создать user с email {}, который уже зарегистрирован", user.getEmail());
            throw new ValidationException("User с таким email уже зарегистрирован");
        } else if(users.values().stream().anyMatch(u -> u.getLogin().equals(user.getLogin()))) {
            log.error("Попытка создать user с логином {}, который уже зарегистрирован", user.getLogin());
            throw new ValidationException("Данный логин занят");
        }
        user.setId(generatedId++);
        if(user.getName() == null) user.setName(user.getLogin());
        users.put(user.getId(), user);
        log.info("Зарегистрирован user с email {}, под логином {}, id={}", user.getEmail(), user.getLogin()
                , user.getId());
        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody @Valid User user) {
        if(!users.containsKey(user.getId())) {
            log.error("Попытка обновить user, который не зарегистрирован");
            throw new ValidationException("Данный user не зарегистрирован");
        }
        users.put(user.getId(), user);
        log.info("Обновлен user с id {}", user.getId());
        return user;
    }

    @DeleteMapping
    public void deleteAllUsers() {
        users.clear();
        generatedId = 1;
        log.info("Все пользователи удалены");
    }
}

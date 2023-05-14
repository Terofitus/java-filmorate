package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private final HashMap<Integer, User> users = new HashMap<>();
    private Integer generatedId = 1;

    @Override
    public List<User> getAllUsers() {
        return List.copyOf(users.values());
    }

    @Override
    public User getUserById(Integer id) {
        if (!users.containsKey(id)) {
            log.error("Попытка получить user по id, который не был добавлен");
            throw new ObjectNotFoundException("Данный user не зарегистрирован");
        }
        return users.get(id);
    }

    @Override
    public User addUserToStorage(User user) {
        if (users.values().stream().anyMatch(u -> u.getEmail().equals(user.getEmail()))) {
            log.error("Попытка создать user с email {}, который уже зарегистрирован", user.getEmail());
            throw new ValidationException("User с таким email уже зарегистрирован");
        } else if (users.values().stream().anyMatch(u -> u.getLogin().equals(user.getLogin()))) {
            log.error("Попытка создать user с логином {}, который уже зарегистрирован", user.getLogin());
            throw new ValidationException("Данный логин занят");
        } else {
            user.setId(generatedId++);
            users.put(user.getId(), user);
            log.info("Зарегистрирован user с email {}, под логином {}, id={}", user.getEmail(), user.getLogin(),
                    user.getId());
            return user;
        }
    }

    @Override
    public User updateUserInStorage(User user) {
        if (!users.containsKey(user.getId())) {
            log.error("Попытка обновить user, который не зарегистрирован");
            throw new ObjectNotFoundException("Данный user не зарегистрирован");
        } else {
            users.put(user.getId(), user);
            log.info("Обновлен user с id {}", user.getId());
            return user;
        }
    }

    @Override
    public void deleteUserFromStorageById(Integer id) {
        if (!users.containsKey(id)) {
            log.error("Попытка удалить user по id, который не был добавлен");
            throw new ObjectNotFoundException("Данный user не зарегистрирован");
        }
        users.remove(id);
    }

    @Override
    public void deleteAllUsers() {
        users.clear();
        generatedId = 1;
        log.info("Все пользователи удалены");
    }
}

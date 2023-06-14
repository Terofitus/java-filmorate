package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Repository("userDbStorage")
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User getUserById(Integer id) {
        User userFromDb = gettingUserById(id);
        if (userFromDb == null) {
            log.error("Попытка получить пользователя по несуществующему id {}", id);
            throw new ObjectNotFoundException(String.format("Пользователь с id %d не был добавлен.", id));
        }
        log.info("Запрошен пользователь по id = {}", id);
        return userFromDb;
    }

    @Override
    public List<User> getAllUsers() {
        String sqlQuery = "SELECT * FROM users AS u LEFT JOIN users_friends AS uf ON u.id = uf.user_friend_id";
        log.info("Запрошенны все пользователи.");
        return jdbcTemplate.query(sqlQuery, this::makeUser);
    }

    @Override
    public User addUserToStorage(User user) {
        String sqlQuery = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        PreparedStatementCreator connection = con -> {
            PreparedStatement stmt = con.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getLogin());
            stmt.setString(3, user.getName());
            stmt.setDate(4, Date.valueOf(user.getBirthday()));
            return stmt;
        };
        jdbcTemplate.update(connection, keyHolder);
        log.info("Был добавлен пользователь с логином {} и email {}.", user.getLogin(), user.getEmail());
        user.setId(keyHolder.getKey().intValue());
        return user;
    }

    @Override
    public User updateUserInStorage(User user) {
        User userFromBd = gettingUserById(user.getId());
        if (userFromBd == null) {
            log.error("Попытка обновления не добавленного пользователя с id {}.", user.getId());
            throw new ObjectNotFoundException(String.format("Пользователь с id %d не "
                    + "был добавлен.", user.getId()));
        }

        String sqlQuery = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?";
        jdbcTemplate.update(sqlQuery, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(),
                user.getId());
        log.info("Был обновлен пользователь с id {}.", user.getId());
        return user;
    }

    @Override
    public void deleteUserFromStorageById(Integer id) {
        User userFromBd = gettingUserById(id);
        if (userFromBd == null) {
            log.error("Попытка удаления не добавленного пользователя с id {}.", id);
            throw new ObjectNotFoundException(String.format("Пользователь с id %d не был добавлен.", id));
        }

        String sqlQuery = "DELETE FROM users WHERE id = ?";
        jdbcTemplate.update(sqlQuery, id);
        log.info("Был удален пользователь с id {}.", id);
    }

    @Override
    public void addUserToFriends(Integer idOfUser, Integer idOfAddedUser) {
        User userFriend = gettingUserById(idOfAddedUser);
        User user = gettingUserById(idOfUser);
        if (userFriend == null) {
            log.error("Попытка добавить в друзья пользователя по несуществующему id {}", idOfAddedUser);
            throw new ObjectNotFoundException(String.format("Пользователь с id %d не был добавлен.", idOfAddedUser));
        }
        if (user == null) {
            log.error("Попытка добавить друга для пользователя с несуществующим id {}", idOfUser);
            throw new ObjectNotFoundException(String.format("Пользователь с id %d не был добавлен.", idOfUser));
        }
        String sqlQuery = "INSERT INTO users_friends (user_id, user_friend_id) VALUES (?, ?)";
        jdbcTemplate.update(sqlQuery, idOfUser, idOfAddedUser);
    }

    @Override
    public void deleteUserFromFriends(Integer idOfUser, Integer idOfUserFriend) {
        User userFriend = gettingUserById(idOfUserFriend);
        User user = gettingUserById(idOfUser);
        if (userFriend == null) {
            log.error("Попытка удалиться из друзей пользователя по несуществующему id {}", idOfUserFriend);
            throw new ObjectNotFoundException(String.format("Пользователь с id %d не был добавлен.", idOfUserFriend));
        }
        if (user == null) {
            log.error("Попытка удалить друга для пользователя с несуществующим id {}", idOfUser);
            throw new ObjectNotFoundException(String.format("Пользователь с id %d не был добавлен.", idOfUser));
        }
        jdbcTemplate.update("DELETE FROM users_friends WHERE user_id = ? AND user_friend_id = ?",
                idOfUser, idOfUserFriend);
    }

    @Override
    public void deleteAllUsers() {
        String sqlQuery = "DELETE FROM users";
        jdbcTemplate.update(sqlQuery);
        log.info("Были удалены все пользователи.");
    }

    @Override
    public List<Integer> getFriendsOfUser(Integer id) {
        gettingUserById(id);
        List<Integer> friends = new ArrayList<>();
        String sqlQuery = "SELECT user_friend_id FROM users_friends WHERE user_id = ?";
        jdbcTemplate.query(sqlQuery, (resultSet, rowInt) -> friends.add(resultSet.getInt("user_friend_id")),
                id);
        return friends;
    }

    private User makeUser(ResultSet resultSet, int rowNum) throws SQLException {
        User user = User.builder().login(resultSet.getString("login"))
            .id(resultSet.getInt("id"))
            .email(resultSet.getString("email"))
            .name(resultSet.getString("name"))
            .birthday(resultSet.getDate("birthday").toLocalDate()).build();
        Set<Integer> friends = new HashSet<>();
        int idFriend;
        while (resultSet.next()) {
            idFriend = resultSet.getInt("user_id");
            friends.add(idFriend);
        }
        user.setFriends(friends);
        return user;
    }

    private User gettingUserById(Integer id) {
        try {
            String sqlQuery = "SELECT * FROM users AS u LEFT JOIN users_friends AS uf" +
                    " ON u.id = uf.user_friend_id WHERE id = ?";
            return jdbcTemplate.queryForObject(sqlQuery, this::makeUser, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}

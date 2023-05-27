package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    User getUserById(Integer id);

    List<User> getAllUsers();

    User addUserToStorage(User user);

    User updateUserInStorage(User user);

    void deleteUserFromStorageById(Integer id);

    void deleteAllUsers();

    void addUserToFriends(Integer idOfUser, Integer idOfAddedUser);

    void deleteUserFromFriends(Integer idOfUser, Integer idOfDeletedUser);

    List<Integer> getFriendsOfUser(Integer id);
}

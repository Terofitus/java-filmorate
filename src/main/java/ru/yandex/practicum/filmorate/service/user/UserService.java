package ru.yandex.practicum.filmorate.service.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserService {
    List<User> getAllUsers();

    User addUserToStorage(User user);

    User updateUserInStorage(User user);

    void deleteUserFromStorageById(Integer id);

    void deleteAllUsers();

    User getUserById(Integer id);

    List<User> getListOfFriends(Integer id);

    void addUserToFriends(Integer idOfUser, Integer idOfAddedUser);

    void deleteUserFromFriends(Integer idOfUser, Integer idOfDeletedUser);

    List<User> getListOfCommonFriends(Integer idOfUser, Integer idOfOtherUser);
}

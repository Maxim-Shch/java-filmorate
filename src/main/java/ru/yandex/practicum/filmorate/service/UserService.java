package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addNewFriend(Integer userId, Integer friendId) {
        User user = userStorage.findById(userId);
        User friend = userStorage.findById(friendId);
        user.setFriends(friendId);
        friend.setFriends(userId);

    }

    public void deleteFriend(Integer userId, Integer friendId) {
        User user = userStorage.findById(userId);
        userStorage.findById(friendId);
        user.deleteFriend(friendId);
    }

    public Collection<User> getUsersFriends(Integer id) {
        User user = userStorage.findById(id);
        return user.getFriends().stream().map(userStorage::findById).collect(Collectors.toList());
    }

    public Collection<User> findCommonFriends(Integer userId, Integer otherUserId) {
        User user = userStorage.findById(userId);
        User otherUser = userStorage.findById(otherUserId);
        Set<Integer> userFriends = user.getFriends();
        Set<Integer> otherUserFriends = otherUser.getFriends();

        List<Integer> commonFriendIds = new ArrayList<>();

        for (Integer id: userFriends) {
            if (otherUserFriends.contains(id)) {
                commonFriendIds.add(id);
            }
        }
        return commonFriendIds.stream().map(userStorage::findById).collect(Collectors.toList());
    }
}

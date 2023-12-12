package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping("users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Collection<User> findAll() { //получение списка всех пользователей.
        return userService.findAll();
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) { //имя для отображения может быть пустым — в таком случае будет использован логин;
        return userService.createUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) { //обновление пользователя;
        return userService.updateUser(user);
    }

    @GetMapping("/{id}")
    public User findById(@PathVariable("id") Integer id) {
        try {
            return userService.findById(id);
        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден", e);
        }
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addNewFriend(@PathVariable("id") Integer id, @PathVariable("friendId") Integer friendId) {
        try {
            userService.addNewFriend(id, friendId);
        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден", e);
        }
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable("id") Integer id, @PathVariable("friendId") Integer friendId) {
        userService.deleteFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public Collection<User> getUsersFriends(@PathVariable("id") Integer id) {
        return userService.getUsersFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> findCommonFriends(@PathVariable("id") Integer id,
                                              @PathVariable("otherId") Integer otherId) {
        return userService.findCommonFriends(id, otherId);
    }
}

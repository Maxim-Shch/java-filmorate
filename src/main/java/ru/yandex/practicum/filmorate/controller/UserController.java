package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("users")
public class UserController {

    private final Map<Integer, User> users = new HashMap<>();
    private Integer generatorId = 0;

    @GetMapping
    public Collection<User> findAll() { //получение списка всех пользователей.
        return users.values();
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) { //имя для отображения может быть пустым — в таком случае будет использован логин;
        validateUser(user);
        user.setId(++generatorId);
        users.put(user.getId(), user);
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) { //обновление пользователя;
        if (users.containsKey(user.getId())) {
            validateUser(user);
            users.put(user.getId(), user);
        } else {
            throw new ValidationException("Пользователя с данным id не существует.");
        }
        return user;
    }

    private void validateUser(User user) {
        if (user.getEmail() == null || !user.getEmail().contains("@")) {
            throw new ValidationException("email не должен быть пустым и должен содержать символ @");
        }
        if (user.getLogin() == null || user.getLogin().contains(" ")) {
            throw new ValidationException("логин не может быть пустым и содержать пробелы");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("дата рождения не может быть в будущем");
        }
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
    }
}

package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.stream.Collectors;


@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public void addNewLike(Integer userId, Integer filmId) {
        Film film = filmStorage.findById(filmId);
        userStorage.findById(userId);
        film.setLikes(filmId);
    }

    public void deleteLike(Integer userId, Integer filmId) {
        Film film = filmStorage.findById(userId);
        filmStorage.findById(filmId);
        film.deleteLike(filmId);
    }

    public Collection<Film> findTheMostPopulars(Integer count) {
        return filmStorage.findAll().stream()
                .sorted((film1, film2) -> film2.getLikes().size() - film1.getLikes().size())
                .limit(count)
                .collect(Collectors.toList());
    }
}

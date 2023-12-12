package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;
import java.util.stream.Collectors;


@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @PostMapping
    public Film addNewFilm(@Valid @RequestBody Film film) { //добавляем фильм
        validateFilm(film);
        return filmStorage.addNewFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) { //обновляем фильм
        validateFilm(film);
        return filmStorage.updateFilm(film);
    }

    @GetMapping
    public Collection<Film> findAll() { //возращает коллекцию всех фильмов
        return filmStorage.findAll();
    }

    @GetMapping("/{id}")
    public Film findById(@PathVariable("id") Integer id) {
        return filmStorage.findById(id);
    }

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
        Film film = filmStorage.findById(filmId);
        userStorage.findById(userId);
        film.deleteLike(filmId);
    }

    public Collection<Film> findTheMostPopulars(Integer count) {
        return filmStorage.findAll()
                .stream()
                .sorted((film1, film2) -> film2.getLikes().size() - film1.getLikes().size())
                .limit(count)
                .collect(Collectors.toList());
    }

    private void validateFilm(Film film) {
        if (film.getName() == null) {
            throw new ValidationException("Название не может быть пустым");
        }
        if (film.getDescription() != null && film.getDescription().length() > 200) {
            throw new ValidationException("Максимальная длина описания — 200 символов");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата релиза должна быть не раньше 28 декабря 1895 года");
        }
        if (film.getDuration() == null || film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        }
    }
}

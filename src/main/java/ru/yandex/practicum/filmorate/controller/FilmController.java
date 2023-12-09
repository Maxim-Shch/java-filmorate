package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/films")
public class FilmController {

    private final Map<Integer, Film> films = new HashMap<>();
    private Integer generatorId = 0;

    @PostMapping
    public Film addNewFilm(@Valid @RequestBody Film film) { //добавляем фильм
        validateFilm(film);
        film.setId(++generatorId);
        films.put(film.getId(), film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) { //обновляем фильм
        if (films.containsKey(film.getId())) {
            validateFilm(film);
            films.put(film.getId(), film);
        } else {
            throw new ValidationException("Фильма с таким id не существует!");
        }
        return film;
    }

    @GetMapping
    public Collection<Film> findAll() { //возращает коллекцию всех фильмов
        return films.values();
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

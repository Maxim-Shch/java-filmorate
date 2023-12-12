package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.Collection;


@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping
    public Film addNewFilm(@Valid @RequestBody Film film) { //добавляем фильм
        return filmService.addNewFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) { //обновляем фильм
        return filmService.updateFilm(film);
    }

    @GetMapping
    public Collection<Film> findAll() { //возращает коллекцию всех фильмов
        return filmService.findAll();
    }

    @GetMapping("/{id}")
    public Film findById(@PathVariable("id") Integer id) {
        try {
            return filmService.findById(id);
        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Фильм не найден", e);
        }
    }

    @PutMapping("/{id}/like/{userId}")
    public void addNewLike(@PathVariable("id") Integer id, @PathVariable("userId") Integer userId) {
        try {
            filmService.addNewLike(userId, id);
        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable("id") Integer id, @PathVariable("userId") Integer userId) {
        try {
            filmService.deleteLike(userId, id);
        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/popular")
    public Collection<Film> findTheMostPopulars(@RequestParam(value = "count", defaultValue = "10",
            required = false) Integer count) {
        return filmService.findTheMostPopulars(count);
    }
}

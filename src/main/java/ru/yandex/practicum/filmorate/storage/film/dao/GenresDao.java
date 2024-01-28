package ru.yandex.practicum.filmorate.storage.film.dao;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;

public interface GenresDao {

    Collection<Genre> getAllGenres();

    Genre getGenreById(Integer genreId);

    Collection<Genre> getGenresOfFilm(Integer filmId);
}

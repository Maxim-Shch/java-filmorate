package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.dao.GenresDao;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class GenreService {

    private final GenresDao genresDao;

    public Collection<Genre> getAllGenres() {
        return genresDao.getAllGenres();
    }

    public Genre getGenreById(Integer genreId) {
        return genresDao.getGenreById(genreId);
    }
}

package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.dao.MpaDao;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class MpaService {

    private final MpaDao mpaDao;

    public Collection<Mpa> getAllMpa() {
        return mpaDao.getAllMpa();
    }

    public Mpa getMpaById(Integer mpaId) {
        return mpaDao.getMpaById(mpaId);
    }
}

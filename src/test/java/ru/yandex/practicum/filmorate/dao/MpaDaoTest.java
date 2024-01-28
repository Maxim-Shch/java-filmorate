package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.dao.MpaDao;
import ru.yandex.practicum.filmorate.storage.film.daoImpl.MpaDaoImpl;

import java.util.Collection;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MpaDaoTest {

    private final JdbcTemplate jdbcTemplate;
    private MpaDao mpaDao;

    @BeforeEach
    void init() {
        mpaDao = new MpaDaoImpl(jdbcTemplate);
    }

    @Test
    public void testGetAllMpa() {
        Collection<Mpa> mpa = mpaDao.getAllMpa();

        assertThat(mpa).isNotNull();
    }

    @Test
    public void testGetMpaById() {
        Mpa savedMpa = mpaDao.getMpaById(1);

        assertThat(savedMpa)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(new Mpa(1, "G", "Нет возрастных ограничений"));
    }
}
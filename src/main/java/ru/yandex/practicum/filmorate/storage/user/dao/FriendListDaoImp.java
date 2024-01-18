package ru.yandex.practicum.filmorate.storage.user.dao;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Objects;

@Component
@AllArgsConstructor
public class FriendListDaoImp implements FriendListDao {

    JdbcTemplate jdbcTemplate;

    @Override
    public void addFriend(Integer userId, Integer friendId) {
        if (Objects.equals(userId, friendId)) {
            throw new ValidationException("Id пользователей не должны совпадать!");
        } else if (checkUserId(userId) || checkUserId(friendId)) {
            throw new NotFoundException("Пользователь не найден");
        }
        String sqlQuery = "INSERT INTO friends(user_id, friend_id, confirmed)" +
                "VALUES (?, ?, ?)";
        jdbcTemplate.update(sqlQuery, userId, friendId, true);
    }

    @Override
    public void deleteFriend(Integer id, Integer friendId) {
        if (checkUserId(id) || checkUserId(friendId)) {
            throw new ValidationException("Введен некорректный id");
        }
        String sqlQuery = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sqlQuery, id, friendId);
    }

    @Override
    public Collection<User> getAll(Integer id) {
        String sql = "SELECT u.user_id AS id,u.user_login,u.user_name,u.user_email,u.user_birthday " +
                "FROM friends AS f " +
                "LEFT JOIN users AS u ON u.user_id = f.friend_id " +
                "WHERE f.user_id = ? AND f.confirmed = TRUE";

        return jdbcTemplate.query(sql, userRowMapper(), id);
    }

    @Override
    public Collection<User> getCommonFriends(Integer id, Integer otherId) {
        String sql = "SELECT  u.* " +
                "FROM friends AS fs " +
                "JOIN users AS u ON fs.friend_id = u.user_id " +
                "WHERE fs.user_id = ? AND fs.friend_id IN (" +
                "SELECT friend_id FROM friends WHERE user_id = ?)";

        return jdbcTemplate.query(sql, userRowMapper(), id, otherId);
    }

    private RowMapper<User> userRowMapper() {
        return (rs, rowNum) -> new User(rs.getInt("user_id"), rs.getString("user_email"),
                rs.getString("user_login"), rs.getString("user_name"),
                rs.getDate("user_birthday").toLocalDate());
    }

    private boolean checkUserId(int id) {
        String sql = "SELECT EXISTS(SELECT 1 FROM users WHERE user_id = ?)";
        return Boolean.FALSE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, id));
    }
}
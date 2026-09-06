package _42.spring.service.repositories;

import _42.spring.service.models.User;
import org.jspecify.annotations.Nullable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import _42.spring.service.models.*;

public class UsersRepositoryJdbcTemplateImpl implements UsersRepository
{
    private final JdbcTemplate jdbcTemplate;

    private RowMapper<User> rowMapper = (rs, numRows) -> (
            new User(rs.getLong("id"), rs.getString("email"))
    );

    public UsersRepositoryJdbcTemplateImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";

        return jdbcTemplate.query(sql, rowMapper, email)
                .stream()
                .findFirst();
    }

    @Override
    public User findById(Long id) {
        String QUERY = "SELECT (id, email) FROM users WHERE id = ?";
        return null;
    }

    @Override
    public List<User> findAll() {
        return List.of();
    }

    @Override
    public void save(User entity) {

    }

    @Override
    public void update(User entity) {

    }

    @Override
    public void delete(Long id) {

    }
}

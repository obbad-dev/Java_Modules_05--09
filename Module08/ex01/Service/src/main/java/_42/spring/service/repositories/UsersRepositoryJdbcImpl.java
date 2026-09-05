package _42.spring.service.repositories;

import _42.spring.service.models.User;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class UsersRepositoryJdbcImpl implements UsersRepository{

    private DataSource dataSource;

    public  UsersRepositoryJdbcImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    @Override
    public Optional<User> findByEmail(String email) {
        String QUERY = "SELECT * FROM users WHERE email = ?";

        try (Connection con = dataSource.getConnection();
            PreparedStatement st = con.prepareStatement(QUERY);)
        {
            st.setString(1, email);
            try(ResultSet rs = st.executeQuery())
            {
                if (rs.next())
                {
                    User user = new User(rs.getLong("id"), rs.getString("email"));
                    return Optional.of(user);
                }
            }
        } catch (SQLException e) {
           e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public User findById(Long id) {
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

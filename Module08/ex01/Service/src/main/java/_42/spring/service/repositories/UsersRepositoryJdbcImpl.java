package _42.spring.service.repositories;

import _42.spring.service.models.User;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
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
        String query = "SELECT * FROM users WHERE id = ?";

        try (Connection con = dataSource.getConnection();
             PreparedStatement st = con.prepareStatement(query)) {
             st.setLong(1, id);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    return new User(rs.getLong("id"), rs.getString("email"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error executing findById for id: " + id, e);
        }
        return null;
    }

    @Override
    public List<User> findAll() {
        String query = "SELECT * FROM users";
        List<User> users = new ArrayList<>();

        try (Connection con = dataSource.getConnection();
             PreparedStatement st = con.prepareStatement(query);
             ResultSet rs = st.executeQuery()) {

            while (rs.next()) {
                users.add(new User(rs.getLong("id"), rs.getString("email")));
            }
            return users;
        } catch (SQLException e) {
            throw new RuntimeException("Error executing findAll", e);
        }
    }

    @Override
    public void save(User entity) {
        String query = "INSERT INTO users (email) VALUES (?)";

        // 1. Tell JDBC to retrieve the generated ID key
        try (Connection con = dataSource.getConnection();
             PreparedStatement st = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            st.setString(1, entity.getEmail());
            st.executeUpdate();

            // 2. Extract the generated ID and assign it to the entity
            try (ResultSet generatedKeys = st.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    entity.setId(generatedKeys.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error executing save", e);
        }
    }

    @Override
    public void update(User entity) {
        String QUERY = "UPDATE users SET email = ? WHERE id = ?";
        try (Connection con = dataSource.getConnection();
             PreparedStatement st = con.prepareStatement(QUERY)) {
            st.setString(1, entity.getEmail());
            st.setLong(2, entity.getId());
            st.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error executing update", e);
        }
    }

    @Override
    public void delete(Long id) {
        String QUERY = "DELETE FROM users WHERE id = ?";
        try (Connection con = dataSource.getConnection();
             PreparedStatement st = con.prepareStatement(QUERY)) {
            st.setLong(1, id);
            st.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error executing delete", e);
        }
    }
}

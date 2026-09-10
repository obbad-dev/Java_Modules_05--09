package _42.spring.service.services;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import _42.spring.service.models.User;
import _42.spring.service.repositories.UsersRepository;

@Service 
public class UsersServiceImpl implements UsersService {

    private  UsersRepository usersRepository;

    @Autowired
    public UsersServiceImpl(@Qualifier ("usersRepositoryJdbcTemplate") UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @Override
    public String signUp(String email) {

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email must not be null or empty");
        }
        String password = UUID.randomUUID().toString().replace("-", "");
        User user = new User(null, email, password);
        usersRepository.save(user);
        return password;
    }
}

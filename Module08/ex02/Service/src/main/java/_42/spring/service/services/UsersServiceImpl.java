package _42.spring.service.services;

import java.util.UUID;

import _42.spring.service.models.User;
import _42.spring.service.repositories.UsersRepository;

public class UsersServiceImpl implements UsersService {

    private  UsersRepository usersRepository;

    public UsersServiceImpl(UsersRepository usersRepository) {
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

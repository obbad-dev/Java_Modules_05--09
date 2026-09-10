package _42.spring.service.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import _42.spring.service.config.TestApplicationConfig;
import _42.spring.service.models.User;
import _42.spring.service.repositories.UsersRepository;

// @ExtendWith (SpringExtension.class)
// @ContextConfiguration (classes = TestApplicationConfig.class)

@SpringJUnitConfig(TestApplicationConfig.class)
public class UsersServiceImplTest {

    @Autowired 
    private UsersService usersService;
    @Autowired
    private UsersRepository usersRepository;

    String email = "oualidobbad@test.com";

    @Test
    public void testSignUpMethod()
    {
        String pass = usersService.signUp(email);
        assertNotNull(pass, "Returned password should not be null");
        assertFalse(pass.trim().isEmpty(), "password must be not Empty");

    }
    @Test
    public void testRepositoryMethod()
    {
        Optional<User> user = usersRepository.findByEmail(email);
        assertTrue(user.isPresent(), "User must be found in the H2 database");
        assertEquals(email, user.get().getEmail(), "Found email must match");
    }

}
package _42.spring.service.services;

import org.h2.command.dml.Call;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

public class UsersServiceImplTest {

//       ### 1. Tell Spring to load your test config
  
//   You need two annotations on the class:
  
//   • @ExtendWith(SpringExtension.class) — integrates JUnit 5 with Spring (tells JUnit to boot a Spring context)
//   • @ContextConfiguration(classes = TestApplicationConfig.class) — tells Spring which config to use
  
//   ### 2. Inject the service
  
//   Use @Autowired to get UsersServiceImpl injected into a field in your test class — just like Spring injects dependencies in production code.
  
//   ### 3. Write a test method
  
//   Annotate a method with @Test (from JUnit 5). Inside it:
  
//   • Call signUp() with a test email
//   • Store the returned password
//   • Use Assertions.assertNotNull(password) to verify it's not null
//   • Use Assertions.assertFalse(password.isEmpty()) to verify it's not empty
}

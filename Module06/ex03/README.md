# Exercise 03: Test for Service

## Exercise Overview
The objective of **Exercise 03** is to implement the **Service layer** for user authentication and test its business logic in complete isolation using **Mockito**.

In this exercise, we do **not** connect to a database. Instead, we define the `UsersRepository` interface, implement `UsersServiceImpl`, and test `UsersServiceImpl.authenticate()` by creating a mock of `UsersRepository`.

---

## Concepts

1. **The Service Layer & Business Logic**
   - The Service layer contains domain rules and orchestrates repositories.
   - It should not know or care whether the repository is backed by PostgreSQL, HSQLDB, or an in-memory mock.
2. **Test Doubles: Mocks & Stubs**
   - **Mock**: A simulated object that mimics the behavior of a real interface without any underlying implementation.
   - **Stubbing (`when(...).thenReturn(...)`)**: Defining the pre-canned response when a mock's method is called.
3. **Behavior Verification (`verify(...)`)**
   - Checking not just return values, but ensuring that specific side-effect methods were invoked on dependencies with exact parameters.

---

## My Implementation

### Source Classes (`src/main/java/fr/s42/`)
- `User.java`: Entity with `id`, `login`, `password`, and `authenticated` (`boolean`).
- `AlreadyAuthenticatedException.java`: Custom unchecked exception thrown if an already authenticated user tries to log in again.
- `UsersRepository.java`: Interface defining persistence contracts:
  ```java
  User findByLogin(String login);
  void update(User user);
  ```
- `UsersServiceImpl.java`:
  - Injected with `UsersRepository` via constructor.
  - `boolean authenticate(String login, String password)`:
    1. Calls `usersRepository.findByLogin(login)`.
    2. If `user.isAuthenticated()`, throws `AlreadyAuthenticatedException`.
    3. If `user.getPassword().equals(password)`:
       - Sets `user.setAuthenticated(true)`.
       - Calls `usersRepository.update(user)`.
       - Returns `true`.
    4. If password does not match: returns `false`.

### Test Suite (`UsersServiceImplTest.java`)
- Annotated with `@ExtendWith(MockitoExtension.class)`.
- `@Mock private UsersRepository usersRepository`: Creates a Mockito mock instance.
- `@InjectMocks private UsersServiceImpl usersServiceImpl`: Injects the mock repository into the service.
- `testAuthenticateCorrectLoginAndPassword()`:
  - Creates an unauthenticated user (`User(1L, "oobbad", "hello123", false)`).
  - Stubs: `when(usersRepository.findByLogin("oobbad")).thenReturn(user)`.
  - Calls: `boolean result = usersServiceImpl.authenticate("oobbad", "hello123")`.
  - Asserts:
    - `assertTrue(result)`
    - `assertTrue(user.isAuthenticated())`
  - Verifies:
    - `verify(usersRepository).update(user)`: Asserts that `update()` was called on the mock with the authenticated user.

---

## How It Works

1. Mockito creates a dynamic proxy implementing `UsersRepository`.
2. When `usersServiceImpl.authenticate("oobbad", "hello123")` runs, it calls `findByLogin("oobbad")`.
3. The mock interceptor intercepts the call and returns the pre-configured `user` object.
4. `UsersServiceImpl` checks credentials, mutates `user.setAuthenticated(true)`, and passes it to `usersRepository.update(user)`.
5. The test asserts the return value is `true` and uses `verify()` to ensure the update method was indeed called.
6. Zero database connections, zero SQL queries, and zero network calls are made. The test completes in milliseconds.

---

## How to Compile

```bash
cd Module06/ex03/Tests
mvn clean compile
```

---

## How to Run

```bash
mvn test
```

To run specifically this test:
```bash
mvn test -Dtest=UsersServiceImplTest
```

---

## Important Things to Understand (for Evaluation / Code Review)

1. **Why mock `UsersRepository` instead of using an embedded database?**
   - Unit tests for business logic should test *only* the business logic. Testing against a database tests both the service and the database driver, making failures harder to diagnose.
2. **What is the difference between `@Mock` and `@InjectMocks`?**
   - `@Mock` creates a mock proxy for a dependency (`UsersRepository`).
   - `@InjectMocks` instantiates the real class being tested (`UsersServiceImpl`) and injects the declared mocks into its constructor/fields.
3. **What does `verify(usersRepository).update(user)` do?**
   - It asserts that the method was invoked exactly once with the given argument during the execution of the test.

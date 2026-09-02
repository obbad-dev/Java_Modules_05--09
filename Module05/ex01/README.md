# Exercise 01: Read / Find

## Exercise Overview
The objective of **Exercise 01** is to implement the **Repository (DAO) pattern** to read and construct entity objects from a PostgreSQL database using JDBC and the **HikariCP** connection pool.

Specifically, we implement `MessagesRepository` with a single method:
```java
Optional<Message> findById(Long id);
```
The method must query the database for a message by its ID, hydrate its `author` (`User`) and `room` (`Chatroom`) with minimal data, and test this in an interactive console application `Program.java`.

---

## Concepts

1. **Repository / DAO Pattern**
   - Separates persistence logic from domain/application logic.
   - Exposes clean interfaces (`MessagesRepository`) hiding SQL queries from the caller.
2. **HikariCP Connection Pool**
   - Creating a database connection requires establishing a physical TCP socket, TLS handshake, and DBMS session authentication.
   - HikariCP maintains a pool of active, reusable connections, minimizing overhead and latency.
3. **SQL Joins & Object Hydration**
   - Hydrating a `Message` requires joining `message`, `users`, and `chatroom` tables in a single query.
   - Extracts columns from `ResultSet` to populate nested Java objects (`message.setAuthor(user)`, `message.setRoom(room)`).
4. **`Optional<T>` Return Type**
   - Encapsulates the possibility that a record with the given ID may not exist, avoiding `null` and `NullPointerException`.

---

## My Implementation

### Key Files
- `MessagesRepository.java`: Interface defining `Optional<Message> findById(Long id)`.
- `MessagesRepositoryJdbcImpl.java`: JDBC implementation accepting `javax.sql.DataSource`.
  - Executes SQL query:
    ```sql
    SELECT * FROM message 
    JOIN users ON message.author_id = users.user_id 
    INNER JOIN chatroom ON message.chatroom_id = chatroom.chatroom_id 
    WHERE message.message_id = ?;
    ```
  - Parses `ResultSet` columns: builds `User` (`user_id`, `login`, `password`), `Chatroom` (`chatroom_id`, `name`), and sets them on `Message`.
  - Formats date string to pattern `yyyy/MM/dd HH:mm` as requested by the subject sample output.
  - Wraps database calls in `try-with-resources` (`Connection`, `PreparedStatement`, `ResultSet`).
- `Program.java`:
  - Configures `HikariConfig` with PostgreSQL credentials (`jdbc:postgresql://174.138.65.179:5432/app`).
  - Instantiates `HikariDataSource` and passes it to `MessagesRepositoryJdbcImpl`.
  - Prompts user for a message ID via `Scanner`.
  - Calls `findById(id)` and prints the resulting `Message` or an error message.

---

## How It Works

1. `Program.main()` initializes `HikariDataSource` with connection settings.
2. User is prompted on console: `Enter a message ID:` -> e.g., `5`.
3. `MessagesRepositoryJdbcImpl.findById(5L)` leases a connection from HikariCP.
4. PreparedStatement sets parameter `1` to `5L` and executes the query.
5. If a row exists, the author `User`, the `Chatroom`, and the `Message` objects are created, linked, and returned wrapped in `Optional.of(message)`.
6. `Program` prints the formatted message string.
7. Resources (`ResultSet`, `PreparedStatement`, `Connection`, `DataSource`) are cleanly closed.

---

## How to Compile

```bash
cd Module05/ex01/Chat
mvn clean compile
```

---

## How to Run

Run via Maven Exec Plugin:
```bash
mvn exec:java
```

Example interaction:
```text
Enter a message ID:
-> 1
message : {
      id= 1,
      author= id = 1, login = alice, password = alice123,
      room= id = 1, name = Java Beginners, owner = null,
      text= Hi everyone, welcome to Java room!,
      date= 2026/04/12 10:00
     }
```

---

## Important Things to Understand (for Evaluation / Code Review)

1. **Why `PreparedStatement` instead of `Statement`?**
   - `PreparedStatement` compiles the SQL query once on the DBMS and safely passes parameters out-of-band, preventing SQL injection.
2. **Why use `Optional<Message>`?**
   - Forces the caller to handle the non-existence of an entity explicitly via `.isPresent()`, `.orElse()`, or `.orElseThrow()`.
3. **What is the purpose of `try-with-resources`?**
   - Automatically closes `AutoCloseable` resources (`Connection`, `Statement`, `ResultSet`) even if an exception is thrown, preventing connection leaks.

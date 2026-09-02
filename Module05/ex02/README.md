# Exercise 02: Create / Save

## Exercise Overview
The objective of **Exercise 02** is to implement entity persistence in `MessagesRepository`:
```java
void save(Message message);
```
The method must insert a new `Message` into the PostgreSQL database, validate that its author and chatroom have valid IDs existing in the database, and assign the DBMS-generated primary key ID back to the passed `message` object. If sub-entities are missing or invalid, a custom unchecked `NotSavedSubEntityException` must be thrown.

---

## Concepts

1. **DBMS Generated Keys (`Statement.RETURN_GENERATED_KEYS`)**
   - When a row is inserted into a table with a `SERIAL PRIMARY KEY`, PostgreSQL generates the ID sequence value.
   - JDBC provides `PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)` to retrieve the newly created ID without executing a second query.
2. **Entity Validation & Referential Integrity**
   - Before inserting a child entity (`Message`), the application must ensure mandatory parent entities (`author` and `room`) exist.
3. **Custom Unchecked Exceptions**
   - Creating `NotSavedSubEntityException extends RuntimeException` to signal application-level validation failures without requiring checked exception handling.
4. **Data Type Mapping (`Timestamp` <-> `LocalDateTime`)**
   - Converting modern Java 8+ `LocalDateTime` to `java.sql.Timestamp` using `Timestamp.valueOf(message.getDateTime())`.

---

## My Implementation

### Key Files
- `NotSavedSubEntityException.java`:
  ```java
  public class NotSavedSubEntityException extends RuntimeException {
      public NotSavedSubEntityException(String message) {
          super(message);
      }
  }
  ```
- `MessagesRepositoryJdbcImpl.java`:
  - Validates `message`, `message.getAuthor()`, `author.getId()`, `message.getRoom()`, and `room.getId()`.
  - Throws `NotSavedSubEntityException` if author or room (or their IDs) are `null`.
  - Prepares SQL:
    ```sql
    INSERT INTO message(author_id, chatroom_id, text, date_time) VALUES (?,?,?,?);
    ```
    using `Statement.RETURN_GENERATED_KEYS`.
  - Binds parameters:
    - `ps.setLong(1, message.getAuthor().getId())`
    - `ps.setLong(2, message.getRoom().getId())`
    - `ps.setString(3, message.getText())`
    - `ps.setTimestamp(4, Timestamp.valueOf(message.getDateTime()))`
  - Executes update, reads `rs = ps.getGeneratedKeys()`, and assigns `message.setId(rs.getLong(1))`.
- `Program.java`:
  - Instantiates valid `User` (e.g. ID `2L`) and `Chatroom` (ID `2L`).
  - Creates a `Message` with `id = null` and `LocalDateTime.now()`.
  - Calls `mRepository.save(message)`.
  - Prints the newly assigned `message.getId()`.

---

## How It Works

1. `Program` creates a `Message` with `id = null` and author/room set.
2. `save()` validates sub-entities. If valid, prepares the insert statement with key return flag.
3. `pre.executeUpdate()` runs the `INSERT` statement in PostgreSQL.
4. PostgreSQL executes the insert and updates sequence `message_message_id_seq`.
5. JDBC receives the generated ID through `pre.getGeneratedKeys()`.
6. `message.setId(newId)` mutates the in-memory object so the caller now has the saved entity's ID.

---

## How to Compile

```bash
cd Module05/ex02/Chat
mvn clean compile
```

---

## How to Run

```bash
mvn exec:java
```

Expected output:
```text
<Generated Message ID, e.g. 6>
```

---

## Important Things to Understand (for Evaluation / Code Review)

1. **Why does `save()` modify the input object?**
   - In standard Java persistence patterns (like JPA/Hibernate), saving a transient entity transitions it into a persisted entity, reflecting the database-assigned identifier.
2. **Why throw `NotSavedSubEntityException` instead of letting PostgreSQL fail with a foreign key constraint error?**
   - Failing fast in Java application code provides clear domain error semantics and avoids unnecessary database network roundtrips.
3. **How does `getGeneratedKeys()` work?**
   - PostgreSQL returns the generated serial column value as part of the statement execution metadata.

# Exercise 03: Update

## Exercise Overview
The objective of **Exercise 03** is to implement full entity updates in `MessagesRepository`:
```java
void update(Message message);
```
The method must fully update an existing message in the database. A crucial requirement of this exercise is the handling of `null` field values: **if a new value of a field in an entity being updated is `null`, this `null` value must be saved to the database**.

---

## Concepts

1. **Full Entity Updates (SQL `UPDATE`)**
   - Updating all fields of a record based on its primary key (`WHERE message_id = ?`).
2. **Handling SQL `NULL` with JDBC**
   - Directly calling `ps.setTimestamp(4, null)` may throw a `NullPointerException` or driver error.
   - JDBC provides `ps.setNull(index, java.sql.Types.TIMESTAMP)` to explicitly bind a SQL `NULL` value for a typed column.
3. **Conversion from `java.sql.Timestamp` to `LocalDateTime`**
   - Storing dates using `ps.setTimestamp(..., Timestamp.valueOf(localDateTime))`.
   - Reading nullable timestamps: checking `res.getTimestamp("date_time") != null` before converting with `.toLocalDateTime()`.

---

## My Implementation

### Key Files
- `MessagesRepository.java`: Adds `void update(Message message)`.
- `MessagesRepositoryJdbcImpl.java`:
  - `update(Message message)`:
    ```sql
    UPDATE message SET author_id = ?, chatroom_id = ?, text = ?, date_time = ? WHERE message_id = ?;
    ```
  - Binds author and chatroom IDs.
  - Text parameter is bound via `ps.setString(3, message.getText())`.
  - **Null timestamp handling**:
    ```java
    if (message.getDateTime() == null)
        ps.setNull(4, java.sql.Types.TIMESTAMP);
    else
        ps.setTimestamp(4, java.sql.Timestamp.valueOf(message.getDateTime()));
    ```
  - Binds `message.getId()` to parameter 5.
  - Executes `ps.executeUpdate()`.
- `Program.java`:
  - Retrieves a message using `mr.findById(3L)`.
  - If present, alters its text (`message.setText("Bye")`) and sets its timestamp to `null` (`message.setDateTime(null)`).
  - Calls `mr.update(message)`.

---

## How It Works

1. `findById(3L)` queries PostgreSQL and instantiates a `Message` object.
2. `message.setText("Bye")` and `message.setDateTime(null)` update the object in memory.
3. `update(message)` executes the parameterized `UPDATE` query.
4. Because `message.getDateTime()` is `null`, `ps.setNull(4, java.sql.Types.TIMESTAMP)` is called.
5. In PostgreSQL, the row with `message_id = 3` now contains text `'Bye'` and column `date_time = NULL`.

---

## How to Compile

```bash
cd Module05/ex03/Chat
mvn clean compile
```

---

## How to Run

```bash
mvn exec:java
```

Verify in database using `psql`:
```bash
psql -d app -c "SELECT * FROM message WHERE message_id = 3;"
```

---

## Important Things to Understand (for Evaluation / Code Review)

1. **Why is `ps.setNull` necessary?**
   - SQL requires type metadata when setting a column to `NULL` so the database query planner knows the datatype of the parameter placeholder.
2. **What happens if `findById` is called on a message with `NULL` `date_time`?**
   - The implementation defensively checks `if (res.getTimestamp("date_time") != null)`, preventing a `NullPointerException` when converting to `LocalDateTime`.

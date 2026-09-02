# Exercise 00: Tables and Entities

## Exercise Overview
The objective of **Exercise 00** is to design the relational database schema and corresponding Java domain entity classes for a Chat application.

The database must model users, chatrooms, messages, and social memberships in PostgreSQL. Each table must use DBMS-generated numeric IDs, and the Java classes must accurately reflect these relationships while implementing Oracle standards, including `equals()`, `hashCode()`, and `toString()`.

---

## Concepts

1. **Relational Database Design (DDL)**
   - `schema.sql` defines tables, primary keys, foreign keys, and constraints.
   - Generates numeric surrogate keys with PostgreSQL `SERIAL PRIMARY KEY`.
2. **Cardinality & Relationships**
   - **One-to-Many**: A `User` can create multiple `Chatrooms` (`owner_id INT REFERENCES Users(user_id)`). A `Chatroom` contains multiple `Messages`.
   - **Many-to-Many**: Users can socialize in multiple chatrooms, and a chatroom has multiple socializing users. This is modeled using an associative/junction table: `User_Chatrooms(user_id, chatroom_id)`.
3. **Domain Entity Modeling**
   - POJOs (`User`, `Chatroom`, `Message`) represent rows as objects.
   - References between classes mirror the foreign key links (`Message` holds a `User author` and `Chatroom room`).
4. **Contract Equality (`equals` & `hashCode`)**
   - Entities in a database are uniquely identified by their primary key `id`.
   - Overriding `equals()` and `hashCode()` ensures consistent behavior when storing entities in hash-based collections (`HashSet`, `HashMap`).

---

## My Implementation

### Relational Schema (`schema.sql`)
The schema defines 4 tables:
- `Users`: `user_id` (SERIAL PK), `login` (VARCHAR 40 UNIQUE NOT NULL), `password` (VARCHAR 30 NOT NULL).
- `Chatroom`: `chatroom_id` (SERIAL PK), `name` (VARCHAR 40 NOT NULL), `owner_id` (INT FK -> `Users(user_id)`).
- `Message`: `message_id` (SERIAL PK), `author_id` (INT FK -> `Users(user_id)`), `chatroom_id` (INT FK -> `Chatroom(chatroom_id)`), `text` (TEXT NOT NULL), `date_time` (TIMESTAMP NOT NULL).
- `User_Chatrooms`: Junction table with composite primary key `(user_id, chatroom_id)` referencing `Users` and `Chatroom`.

### Seed Data (`data.sql`)
Provides at least 5 records per table:
- 5 users (`alice`, `bob`, `charlie`, `diana`, `eric`).
- 5 chatrooms (`Java Beginners`, `PostgreSQL Help`, `42 Network`, `Movie Club`, `Daily Standup`).
- 5 messages across rooms.
- 8 social memberships linking users to multiple rooms.

### Java Domain Models (`fr.s42.chat.models`)
- `User.java`: Contains `id`, `login`, `password`, `createdRooms` (`List<Chatroom>`), and `socializedRooms` (`List<Chatroom>`).
- `Chatroom.java`: Contains `id`, `name`, `onwer` (`User`), and `messages` (`List<Message>`).
- `Message.java`: Contains `id`, `author` (`User`), `room` (`Chatroom`), `text` (`String`), and `dateTime` (`LocalDateTime`).
- All models implement proper getters, setters, `equals()` checking `Objects.equals(this.id, other.id)`, and `hashCode()` based on `id`.

---

## How It Works

1. When `schema.sql` is executed against PostgreSQL, the DBMS allocates tables and constraints.
2. When `data.sql` is executed, initial records are inserted, automatically advancing sequence generators for IDs.
3. In Java, objects of `User`, `Chatroom`, and `Message` are instantiated to hold mapped data when communicating with PostgreSQL in later exercises.

---

## How to Compile

Navigate to the exercise project directory:
```bash
cd Module05/ex00/Chat
mvn clean compile
```

---

## How to Run

To test the database schema and sample data using PostgreSQL CLI (`psql`):
```bash
# Create database if not exists
createdb app

# Apply schema and seed data
psql -d app -f src/main/resources/schema.sql
psql -d app -f src/main/resources/data.sql

# Verify tables
psql -d app -c "SELECT * FROM Users;"
psql -d app -c "SELECT * FROM Chatroom;"
psql -d app -c "SELECT * FROM Message;"
psql -d app -c "SELECT * FROM User_Chatrooms;"
```

---

## Important Things to Understand (for Evaluation / Code Review)

1. **Why use an associative table (`User_Chatrooms`)?**
   - Relational databases cannot store collections directly in a single column without violating First Normal Form (1NF). A junction table decomposes a Many-to-Many relationship into two One-to-Many relationships.
2. **Why base `equals()` and `hashCode()` on `id`?**
   - In persistence frameworks, two entity instances representing the same database row must be considered equal even if their transient attributes differ.
3. **What is `SERIAL` in PostgreSQL?**
   - `SERIAL` creates an integer column backed by an auto-incrementing sequence generator (`nextval(...)`).

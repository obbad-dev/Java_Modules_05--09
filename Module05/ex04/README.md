# Exercise 04: Find All

## Exercise Overview
The objective of **Exercise 04** is to implement pagination and complex object graph retrieval in `UsersRepository`:
```java
List<User> findAll(int page, int size);
```
The method retrieves a paginated slice of users (`size` users from page `page`), including their created chatrooms and socialized chatrooms.

**Strict Requirement**: The entire operation must be executed using a **SINGLE SQL QUERY**. Using separate queries per user ($N+1$ query problem) is strictly forbidden.

---

## Concepts

1. **Pagination (`LIMIT` / `OFFSET`)**
   - Dividing large result sets into pages. Page numbering starts from 0:
     $$\text{OFFSET} = \text{page} \times \text{size}, \quad \text{LIMIT} = \text{size}$$
2. **The $N+1$ Query Problem**
   - A common performance antipattern where an application executes 1 query to fetch $N$ parent records, followed by $N$ separate queries to fetch children for each parent.
3. **Common Table Expressions (CTE)**
   - A PostgreSQL feature (`WITH PaginatedUsers AS (...)`) creating a temporary result set within the query execution.
   - Allows paginating the base `Users` table *first*, and then joining child tables onto only those paginated users.
4. **Relational Result Set to Object Graph Mapping**
   - Joining a 1-to-many relationship produces multiple rows for the same parent user.
   - Using a `LinkedHashMap<Long, User>` preserves pagination order while aggregating created and social rooms into the user's collections without duplicating `User` instances.

---

## My Implementation

### Single SQL Query with PostgreSQL CTE
```sql
WITH PaginatedUsers AS (
    SELECT * FROM Users 
    ORDER BY user_id 
    LIMIT ? OFFSET ?
) 
SELECT 
    PaginatedUsers.user_id, PaginatedUsers.login, PaginatedUsers.password, 
    created_room.chatroom_id AS c_id, created_room.name AS c_name, 
    social_room.chatroom_id AS s_id, social_room.name AS s_name 
FROM PaginatedUsers 
LEFT JOIN Chatroom AS created_room ON PaginatedUsers.user_id = created_room.owner_id 
LEFT JOIN User_Chatrooms ON PaginatedUsers.user_id = User_Chatrooms.user_id 
LEFT JOIN Chatroom AS social_room ON User_Chatrooms.chatroom_id = social_room.chatroom_id;
```

### Result Mapping Logic (`UsersRepositoryJdbcImpl.java`)
- Binds `size` to `LIMIT` and `size * page` to `OFFSET`.
- Iterates through `ResultSet`:
  1. If `user_id` is not yet in `Map<Long, User> mapUsers`, creates a new `User` with empty `createdRooms` and `socializedRooms` lists.
  2. If `c_id != 0`, checks if already in `createdRooms`; if not, adds a new `Chatroom(c_id, c_name, targetUser, ...)`.
  3. If `s_id != 0`, checks if already in `socializedRooms`; if not, adds a new `Chatroom(s_id, s_name, targetUser, ...)`.
- Returns `new ArrayList<>(mapUsers.values())`.

### Demonstration (`Program.java`)
- Fetches `findAll(0, 5)` (Page 0, Size 5).
- Iterates over the returned users and prints user details, created rooms, and socialized rooms in a clean formatted view.

---

## How It Works

1. The database computes `PaginatedUsers` by ordering users by ID and applying `LIMIT` and `OFFSET`.
2. It left-joins `Chatroom` (for rooms created by the user) and `User_Chatrooms` + `Chatroom` (for rooms where the user socializes).
3. Even if a user has 3 created rooms and 4 social rooms (resulting in multiple rows in `ResultSet`), the `LinkedHashMap` groups them under the exact same `User` instance.
4. The caller receives a clean `List<User>` where each user has its full collections populated.

---

## How to Compile

```bash
cd Module05/ex04/Chat
mvn clean compile
```

---

## How to Run

```bash
mvn exec:java
```

Sample output:
```text
--- Fetching Page 0, Size 5 ---

👤 User ID: 1 | Login: alice
   🏠 Created Rooms:
      - [1] Java Beginners
   🌐 Social Rooms:
      - [1] Java Beginners
      - [2] PostgreSQL Help

👤 User ID: 2 | Login: bob
   🏠 Created Rooms:
      - [2] PostgreSQL Help
   🌐 Social Rooms:
      - [1] Java Beginners
      - [2] PostgreSQL Help
...
✅ Test Complete!
```

---

## Important Things to Understand (for Evaluation / Code Review)

1. **Why use a CTE instead of a simple `LIMIT` / `OFFSET` at the end of the query?**
   - If you do `LEFT JOIN` first and `LIMIT 5` at the end, a single user with 5 rooms would consume all 5 rows, returning only 1 user instead of 5 users! The CTE guarantees exactly `size` users are selected first before joining.
2. **Why `LinkedHashMap`?**
   - `HashMap` does not guarantee insertion order. `LinkedHashMap` preserves the exact ordering produced by the SQL `ORDER BY user_id`.
3. **How is the requirement of "no subentity dependencies" respected?**
   - Subentities (`Chatroom`) have an empty list of messages (`new ArrayList<>()`), matching the subject rule that rooms must not load internal messages.

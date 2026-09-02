# Exercise 00: Work with Classes

## Exercise Overview
The objective of **Exercise 00** is to build an interactive command-line application that dynamically explores and manipulates Java classes at runtime using the **Java Reflection API**.

The application must:
1. List available classes in the `classes` package (`User`, `Car`).
2. Allow the user to select a class to inspect.
3. Display the class's declared fields (type and name) and methods (return type, name, and parameter list).
4. Dynamically instantiate an object by prompting the user for initial field values.
5. Allow the user to dynamically update any chosen private field.
6. Allow the user to dynamically invoke any chosen method with custom input arguments and display its return value.

---

## Concepts

1. **Dynamic Class Loading (`Class.forName`)**
   - Loading a class into the JVM by its fully-qualified string name at runtime.
2. **Metadata Introspection (`getDeclaredFields`, `getDeclaredMethods`)**
   - Retrieving all fields and methods declared directly in the class, regardless of their access modifiers (`private`, `protected`, `public`).
3. **Reflective Object Instantiation (`Constructor.newInstance`)**
   - Instantiating an object dynamically without using the `new` operator directly in code.
4. **Accessing Private Members (`setAccessible(true)`)**
   - Bypassing Java language access control checks to read or modify `private` fields and invoke `private` methods.
5. **Dynamic Method Invocation (`Method.invoke`)**
   - Calling a method dynamically on an object instance and capturing its return value.

---

## My Implementation

### Domain Classes (`src/main/java/classes/`)
- `User.java`: Contains `firstName` (`String`), `lastName` (`String`), `height` (`int`), default constructor, parameterized constructor, `grow(int value)` method, and overridden `toString()`.
- `Car.java`: Contains `brand` (`String`), `model` (`String`), `mileage` (`double`), constructors, `drive(double distance)` method, and `toString()`.

### Core Application (`src/main/java/app/Program.java`)
- `printingFieldsAndMethods(Class<?> clazz)`:
  - Iterates over `clazz.getDeclaredFields()`, printing `field.getType().getSimpleName()` and `field.getName()`.
  - Iterates over `clazz.getDeclaredMethods()`, filtering out `toString()`, formatting parameter types into `(Type1, Type2)`, and displaying return type and name.
- `creatObject(Scanner sc, Class<?> clazz)`:
  - Instantiates a base instance using `clazz.getDeclaredConstructor().newInstance()`.
  - Iterates over declared fields, prompts user for each field's value, calls `field.setAccessible(true)`, parses the input string according to the field type (`String`, `int`, `double`), and updates the field via `field.set(obj, value)`.
- `updateObject(Scanner sc, Object obj, Class<?> clazz)`:
  - Prompts user for the field name to change.
  - Retrieves `clazz.getDeclaredField(fieldName)`.
  - Enables accessibility, parses the new input value, sets it on `obj`, and prints the updated object.
- `callMethod(Scanner sc, Class<?> clazz, Object obj)`:
  - Parses method name and parameter types from user input (e.g. `grow(int)`).
  - Finds `clazz.getDeclaredMethod(name, paramTypes)`.
  - Prompts the user for argument values, invokes `method.invoke(obj, args)`, and prints the result if the return type is not `void`.

---

## How It Works

1. User enters `User`.
2. `Class.forName("classes.User")` loads the class metadata.
3. `printingFieldsAndMethods` prints fields (`firstName`, `lastName`, `height`) and methods (`int grow(int)`).
4. `creatObject` prompts for `firstName`, `lastName`, and `height`, creating a populated `User` instance.
5. `updateObject` asks which field to change (e.g. `firstName` -> `Name`).
6. `callMethod` asks for the method signature to call (e.g. `grow(int)`), prompts for the argument value (e.g. `10`), calls `grow.invoke(user, 10)`, and prints the new height `195`.

---

## How to Compile

```bash
cd Module07/ex00/Reflection
mvn clean compile
```

---

## How to Run

```bash
mvn exec:java
```

Example session:
```text
Classes:
  User
  Car
---------------------
Enter class name:
-> User
---------------------
fields:
    String firstName
    String lastName
    int height
methods:
    int grow (int)
---------------------
Let's create an object.
firstName
-> Alice
lastName
-> Smith
height
-> 175
Object created: User[firstName='Alice', lastName='Smith', height=175]
---------------------
Enter name of the field for changing:
-> firstName
Enter String value:
-> Bob
Object updated: User[firstName='Bob', lastName='Smith', height=175]
---------------------
Enter name of the method for call:
-> grow(int)
Enter int value:
-> 15
Method returned:
190
---------------------
```

---

## Important Things to Understand (for Evaluation / Code Review)

1. **What is the difference between `getFields()` and `getDeclaredFields()`?**
   - `getFields()` returns only `public` fields (including inherited ones).
   - `getDeclaredFields()` returns *all* fields declared directly in that class (`private`, `protected`, `package-private`, `public`), but excludes inherited fields.
2. **Why is `field.setAccessible(true)` needed?**
   - Java's access control prevents direct external modification of `private` fields. `setAccessible(true)` suppresses JVM language access checks.
3. **How does `Method.invoke()` handle return values for primitives?**
   - Reflection boxes primitive return types (e.g., `int` becomes `java.lang.Integer`). If a method returns `void`, `invoke()` returns `null`.

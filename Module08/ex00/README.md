# Exercise 00: Spring Context

## Exercise Overview
The objective of **Exercise 00** is to implement a **loosely-coupled system** of components (beans) using the **Spring IoC container** with **XML-based configuration** (`context.xml`).

The application defines a layered architecture of interfaces and implementations:
- `PreProcessor` (message pre-processing) → `Renderer` (output channel) → `Printer` (message formatting and delivery).
- Each layer depends on abstractions (interfaces), not concrete implementations — dependencies are wired by the Spring container via `context.xml`.

---

## Concepts

1. **Inversion of Control (IoC)**
   - Instead of classes creating their own dependencies with `new`, the Spring container creates all objects and injects dependencies based on the XML configuration.
2. **`ClassPathXmlApplicationContext`**
   - Bootstraps the Spring IoC container by parsing `context.xml` from the classpath, creating `BeanDefinition` objects, and instantiating all defined beans.
3. **Constructor Injection (`<constructor-arg ref="...">`)**
   - Spring calls the bean's constructor and passes the referenced bean as an argument. This is the preferred DI pattern — dependencies are immutable and required at creation time.
4. **Setter Injection (`<property name="..." value="...">`)**
   - Spring calls a setter method after construction to inject a value. Used for optional or configurable properties like `prefix`.
5. **Bean Retrieval (`getBean`)**
   - `context.getBean("beanId", Type.class)` retrieves a managed bean from the container by its ID and expected type.

---

## My Implementation

### Interfaces & Implementations

| Layer | Interface | Implementations |
| :--- | :--- | :--- |
| Pre-Processing | `PreProcessor` | `PreProcessorToUpperImpl` (uppercase), `PreProcessorToLowerImpl` (lowercase) |
| Rendering | `Render` | `RenderErrImpl` (System.err), `RenderStandardImpl` (System.out) |
| Printing | `Printer` | `PrinterWithPrefixImpl` (adds prefix), `PrinterWithDateTimeImpl` (adds timestamp) |

### XML Configuration (`src/main/resources/context.xml`)
```xml
<bean id="preProcessorToUpper" class="...PreProcessorToUpperImpl" />

<bean id="renderErr" class="...RenderErrImpl">
    <constructor-arg ref="preProcessorToUpper"/>
</bean>

<bean id="printerWithPrefix" class="...PrinterWithPrefixImpl">
    <constructor-arg ref="renderErr"/>
    <property name="prefix" value="Prefix"/>
</bean>
```

### Application (`src/main/java/.../app/Main.java`)
```java
ApplicationContext cnx = new ClassPathXmlApplicationContext("context.xml");
Printer printer = cnx.getBean("printerWithPrefix", Printer.class);
printer.print("Hello!");
```

---

## How It Works

1. `Main` creates a `ClassPathXmlApplicationContext` which reads `context.xml`.
2. Spring instantiates `PreProcessorToUpperImpl`, then `RenderErrImpl` (injecting the preprocessor via constructor), then `PrinterWithPrefixImpl` (injecting the renderer via constructor + setting prefix via setter).
3. `Main` retrieves the `printerWithPrefix` bean and calls `print("Hello!")`.
4. The message flows: `PrinterWithPrefixImpl` → `RenderErrImpl` → `PreProcessorToUpperImpl` → output: `PREFIX HELLO` via `System.err`.

---

## How to Compile

```bash
cd Module08/ex00/Spring
mvn clean compile
```

---

## How to Run

```bash
mvn exec:java
```

Expected output (on stderr):
```text
PREFIX HELLO
```

---

## Important Things to Understand (for Evaluation / Code Review)

1. **What is the difference between constructor injection and setter injection?**
   - Constructor injection provides dependencies at creation time — the object is never in an incomplete state. Setter injection allows optional dependencies to be set after construction. Constructor injection is preferred for required dependencies.
2. **What does `ClassPathXmlApplicationContext` do internally?**
   - It reads `context.xml`, parses `<bean>` elements into `BeanDefinition` objects stored in a `BeanDefinitionRegistry`, then instantiates them in dependency order, resolving `<constructor-arg ref>` and `<property>` references.
3. **Why program to interfaces (`Printer`, `Render`, `PreProcessor`) instead of concrete classes?**
   - It enables swapping implementations by changing only `context.xml` — no Java code changes needed. For example, switching from `RenderErrImpl` to `RenderStandardImpl` requires changing one XML attribute.

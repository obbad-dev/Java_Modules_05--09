# Exercise 01: Annotations - SOURCE

## Exercise Overview
The objective of **Exercise 01** is to create a **compile-time annotation processor** (`HtmlProcessor`) that generates HTML form files during compilation (`mvn clean compile`).

We define two custom annotations:
- `@HtmlForm`: Placed on classes to specify the output file name, form action, and HTTP method.
- `@HtmlInput`: Placed on fields to specify HTML input tag attributes (`type`, `name`, `placeholder`).

When `mvn clean compile` runs, the annotation processor scans all classes annotated with `@HtmlForm` (such as `UserForm`), reads the field annotations, and outputs the resulting `.html` file directly into `target/classes/`.

---

## Concepts

1. **Annotation Retention Policies (`@Retention`)**
   - `@Retention(RetentionPolicy.SOURCE)` means the annotation is only retained in source code and is not compiled into the `.class` bytecode.
   - It is purely intended for compiler-level tools and annotation processors.
2. **Annotation Processing API (`javax.annotation.processing`)**
   - Standard Java API enabling compiler plugins (`AbstractProcessor`) that inspect declarations before bytecode generation.
3. **Compile-Time File Generation (`Filer`)**
   - The `Filer` interface creates new source files, class files, or auxiliary resources (`StandardLocation.CLASS_OUTPUT`).
4. **Service Provider Interface & AutoService**
   - For `javac` to discover an annotation processor, it must be declared in `META-INF/services/javax.annotation.processing.Processor`.
   - Google's `@AutoService(Processor.class)` automatically generates this file at build time.

---

## My Implementation

### Annotations (`src/main/java/annotations/`)
- `HtmlForm.java`:
  ```java
  @Retention(RetentionPolicy.SOURCE)
  @Target(ElementType.TYPE)
  public @interface HtmlForm {
      String fileName();
      String action();
      String method();
  }
  ```
- `HtmlInput.java`:
  ```java
  @Retention(RetentionPolicy.SOURCE)
  @Target(ElementType.FIELD)
  public @interface HtmlInput {
      String type();
      String name();
      String placeholder();
  }
  ```

### Example Annotated Form (`src/main/java/forms/UserForm.java`)
```java
@HtmlForm(fileName = "user_form.html", action = "/users", method = "post")
public class UserForm {
    @HtmlInput(type = "text", name = "first_name", placeholder = "Enter First Name")
    private String firstName;

    @HtmlInput(type = "text", name = "last_name", placeholder = "Enter Last Name")
    private String lastName;

    @HtmlInput(type = "password", name = "password", placeholder = "Enter Password")
    private String password;
}
```

### Processor (`src/main/java/processor/HtmlProcessor.java`)
- Annotated with `@AutoService(Processor.class)`, `@SupportedSourceVersion(SourceVersion.RELEASE_17)`, and `@SupportedAnnotationTypes({"annotations.HtmlForm", "annotations.HtmlInput"})`.
- In `process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv)`:
  1. Finds all elements annotated with `@HtmlForm` using `roundEnv.getElementsAnnotatedWith(HtmlForm.class)`.
  2. Casts element to `TypeElement` and extracts `@HtmlForm` attributes (`action`, `method`, `fileName`).
  3. Builds HTML `<form action="..." method="...">` opening tag.
  4. Loops through enclosed elements (`tE.getEnclosedElements()`), checking for `@HtmlInput`.
  5. Appends `<input type="..." name="..." placeholder="...">` tags.
  6. Appends submit button `<input type="submit" value="Send">` and closing `</form>`.
  7. Uses `processingEnv.getFiler().createResource(StandardLocation.CLASS_OUTPUT, "", htmlForm.fileName())` to write the HTML file.

---

## How It Works

1. You compile the project using `mvn clean compile`.
2. The Java compiler runs `auto-service`, which generates the ServiceLoader descriptor `META-INF/services/javax.annotation.processing.Processor` containing `processor.HtmlProcessor`.
3. `HtmlProcessor` processes classes annotated with `@HtmlForm` (such as `UserForm`), parses all `@HtmlInput` field annotations, and constructs HTML markup.
4. Using `processingEnv.getFiler().createResource(StandardLocation.CLASS_OUTPUT, "", htmlForm.fileName())`, it outputs `user_form.html` into `target/classes/`.

---

## How to Compile

Navigate to the exercise directory and compile with Maven:
```bash
cd Module07/ex01/Annotations
mvn clean compile
```

---

## How to Run & Verify

To run the annotation processor on `UserForm.java` and generate the HTML form:
```bash
javac -cp "target/classes:$(mvn -q exec:exec -Dexec.executable=echo -Dexec.args="%classpath")" \
  -processor processor.HtmlProcessor \
  -d target/classes \
  src/main/java/forms/UserForm.java
```

Verify that `user_form.html` was generated in `target/classes/`:
```bash
cat target/classes/user_form.html
```

Expected content:
```html
<form action="/users" method="post">
<input type="text" name="first_name" placeholder="Enter First Name">
<input type="text" name="last_name" placeholder="Enter Last Name">
<input type="password" name="password" placeholder="Enter Password">
<input type="submit" value="Send"></form>
```

---

## Important Things to Understand (for Evaluation / Code Review)

1. **Why use `RetentionPolicy.SOURCE` instead of `RUNTIME`?**
   - The HTML generation happens exclusively during compilation. Keeping these annotations in the runtime bytecode would waste memory with metadata that is never used at runtime.
2. **What is `AutoService`?**
   - Google AutoService generates the provider-configuration file in `META-INF/services/javax.annotation.processing.Processor` automatically, which registers the processor with the Java compiler.
3. **What is the `Filer`?**
   - A compiler utility that lets annotation processors safely create new files that the compiler manages and places into build output directories.

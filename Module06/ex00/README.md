# Exercise 00: First Tests

## Exercise Overview
The objective of **Exercise 00** is to write comprehensive unit tests using **JUnit 5 Jupiter** for a mathematical helper class: `NumberWorker`.

The exercise requires implementing:
- `boolean isPrime(int number)`: Checks primality; throws an unchecked `IllegalNumberException` for values $\le 1$.
- `int digitsSum(int number)`: Returns the sum of digits of a given integer.
- `NumberWorkerTest`: Tests these methods using parameterized tests (`@ValueSource` and `@CsvFileSource`).

---

## Concepts

1. **Unit Testing**
   - Testing individual methods in isolation to verify deterministic outputs for given inputs.
2. **Parameterized Tests (`@ParameterizedTest`)**
   - Executing the same test method multiple times with different arguments.
   - `@ValueSource`: Feeds an array of literal values (e.g. ints) into the test method.
3. **CSV-Driven Testing (`@CsvFileSource`)**
   - Reading external test cases from a CSV file (`data.csv`).
   - Cleanly separates test code from test data sets.
4. **Exception Testing (`assertThrows`)**
   - Verifying that invalid inputs correctly trigger expected runtime exceptions.

---

## My Implementation

### Source Classes (`src/main/java/fr/s42/numbers/`)
- `IllegalNumberException.java`: Custom unchecked exception extending `RuntimeException`.
- `NumberWorker.java`:
  - `isPrime(int number)`:
    - Checks `if (number <= 1) throw new IllegalNumberException(...)`.
    - Loops from `2` up to `number - 1` checking `if (number % i == 0) return false`.
    - Returns `true` if no divisor is found.
  - `digitsSum(int number)`:
    - Takes `Math.abs(number)` to handle potential negative numbers cleanly.
    - Loops while `number > 0`, extracting digits with `number % 10` and dividing by 10.

### Test Class (`src/test/java/fr/s42/numbers/NumberWorkerTest.java`)
- `isPrimeForPrimes(int numbers)`:
  - `@ValueSource(ints = {2, 3, 5, 7, 11, 13})`
  - Asserts `assertTrue(numberWorker.isPrime(numbers))`.
- `isPrimeForNotPrimes(int nb)`:
  - `@ValueSource(ints = {4, 6, 8, 9, 10})`
  - Asserts `assertFalse(numberWorker.isPrime(nb))`.
- `isPrimeForIncorrectNumber(int nb)`:
  - `@ValueSource(ints = {-1, 0, -444, -77777})`
  - Asserts `assertThrows(IllegalNumberException.class, () -> numberWorker.isPrime(nb))`.
- `testDigitsSumCsvFile(int input, int expect)`:
  - `@CsvFileSource(resources = "/data.csv", numLinesToSkip = 1)`
  - Reads numbers and expected digit sums from `src/test/resources/data.csv`.
  - Asserts `assertEquals(expect, numberWorker.digitsSum(input))`.

---

## How It Works

1. Maven Surefire runner discovers `@ParameterizedTest` methods in `NumberWorkerTest`.
2. For each method:
   - JUnit feeds each value from `@ValueSource` into the test parameter.
   - For `testDigitsSumCsvFile`, JUnit parses `data.csv`, converts values to `int`, skips the header line (`numLinesToSkip = 1`), and runs an assertion per row.
3. If any assertion fails, Surefire reports the exact parameter that failed.

---

## How to Compile

```bash
cd Module06/ex00/Tests
mvn clean compile
```

---

## How to Run

Execute the automated test suite:
```bash
mvn test
```

Expected output:
```text
[INFO] Running fr.s42.numbers.NumberWorkerTest
[INFO] Tests run: 24, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.551 s -- in fr.s42.numbers.NumberWorkerTest
[INFO] Results:
[INFO] Tests run: 24, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

---

## Important Things to Understand (for Evaluation / Code Review)

1. **Why use `@ParameterizedTest` instead of writing multiple `@Test` methods or a `for` loop?**
   - Writing separate methods creates massive code duplication.
   - A `for` loop inside a single `@Test` stops on the first failure, masking later failures. Parameterized tests run each input as an independent test case.
2. **Why is `numLinesToSkip = 1` used in `@CsvFileSource`?**
   - To skip the header line (e.g. `# number, digitsSum`) in `data.csv`.

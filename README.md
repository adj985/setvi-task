# setvi-task API Tests

This repository contains automated API tests for Setvi endpoints using Java, Maven, TestNG, and Rest Assured.

## Tech Stack

- Java 21
- Maven
- TestNG
- Rest Assured
- Gson

## Prerequisites

Install the following tools before running tests:

1. JDK 21
2. Maven 3.9+ (or any recent Maven 3.x)
3. Git (optional, for cloning/version control)
4. Internet access to reach the target API environment

## Project Structure

- `src/main/java/baseApi` - API client helpers, constants, response helpers, DTOs
- `src/test/java/tests` - test scenarios grouped by bug category (`positive`, `negative`)
- `testng.xml` - TestNG suite configuration used by Maven Surefire
- `target/surefire-reports` - generated test execution reports

## Run Tests

From project root:

```bash
mvn test
```

Quiet mode:

```bash
mvn test -q
```

Run a single test class:

```bash
mvn "-Dtest=tests.bugs.positive.UploadFreeTextPositiveBugsTest" test
```

Run one test method:

```bash
mvn "-Dtest=tests.bugs.positive.UploadFreeTextPositiveBugsTest#uploadFreeTextFirstMatchedProductShouldContainCoreMetadataTest" test
```

## Test Reports

After execution, open:

- `target/surefire-reports/index.html`

Additional useful report files:

- `target/surefire-reports/emailable-report.html`
- `target/surefire-reports/testng-results.xml`

## IntelliJ IDEA Notes

This project was developed and run in **IntelliJ IDEA**.

Typical IntelliJ workflow:

1. Open the project folder.
2. Ensure Project SDK is set to Java 21.
3. Import/sync Maven dependencies from `pom.xml`.
4. Run tests from:
   - Maven tool window (`Lifecycle -> test`), or
   - right-click `testng.xml` and run TestNG suite, or
   - right-click individual test classes/methods.

## Known Notes

- Some tests intentionally validate bug scenarios and may fail depending on current backend behavior.
- Test output may include full API error payloads to improve debugging.


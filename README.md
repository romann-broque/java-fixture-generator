# fixtures

[![Maven Central](https://img.shields.io/maven-central/v/io.github.romann-broque/fixture-annotations.svg?label=maven%20central)](https://central.sonatype.com/artifact/io.github.romann-broque/fixture-annotations)
[![Build](https://github.com/romann-broque/fixtures/actions/workflows/release.yml/badge.svg?branch=main)](https://github.com/romann-broque/fixtures/actions)
[![Javadoc](https://javadoc.io/badge2/io.github.romann-broque/fixture-annotations/javadoc.svg)](https://javadoc.io/doc/io.github.romann-broque/fixture-annotations)
[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](./LICENSE)

Generate **Java test fixtures** from annotated `DataSet` classes at **compile time** (annotation processor).
Less boilerplate, more readable tests.

- **`fixture-annotations`** — public annotations to mark your DataSet classes
- **`fixture-processor`** — the annotation processor that generates fixture builders

> Java 21+, Gradle 8+, Maven 3.9+. Works with plain JUnit and Spring Boot.

---

## Installation

### Gradle (Java)

```groovy
repositories { mavenCentral() }

// Generate fixtures for application sources (src/main/java)
dependencies {
  implementation "io.github.romann-broque:fixture-annotations"
  annotationProcessor "io.github.romann-broque:fixture-processor"
}

// OR generate fixtures for tests only (src/test/java)
dependencies {
  testImplementation "io.github.romann-broque:fixture-annotations"
  testAnnotationProcessor "io.github.romann-broque:fixture-processor"
}

```

### Kotlin (KAPT)

```kotlin
dependencies {
  implementation("io.github.romann-broque:fixture-annotations:x.y.z")
  kapt("io.github.romann-broque:fixture-processor:x.y.z")

  // For tests:
  testImplementation("io.github.romann-broque:fixture-annotations:x.y.z")
  kaptTest("io.github.romann-broque:fixture-processor:x.y.z")
}

```

### Maven

```xml
<dependencies>
  <!-- Generate during main compilation -->
  <dependency>
    <groupId>io.github.romann-broque</groupId>
    <artifactId>fixture-annotations</artifactId>
    <version>x.y.z</version>
  </dependency>
  <dependency>
    <groupId>io.github.romann-broque</groupId>
    <artifactId>fixture-processor</artifactId>
    <version>x.y.z</version>
    <scope>provided</scope>
  </dependency>

  <!-- OR generate during test compilation -->
  <!--
  <dependency>
    <groupId>io.github.romann-broque</groupId>
    <artifactId>fixture-annotations</artifactId>
    <version>x.y.z</version>
    <scope>test</scope>
  </dependency>
  <dependency>
    <groupId>io.github.romann-broque</groupId>
    <artifactId>fixture-processor</artifactId>
    <version>x.y.z</version>
    <scope>test</scope>
  </dependency>
  -->
</dependencies>

```

---

## Usage example

Assuming you have a `Customer` model you want to test:

```java
package org.example.testfixtures.models;

import java.time.LocalDate;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.testfixtures.exceptions.CustomerException;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Customer {
  private String firstName;
  private String lastName;
  private String email;
  private LocalDate birthDate;
  private String phoneNumber;
  private String address;

  public static Customer create(final String firstName,
                                final String lastName,
                                final String email,
                                final LocalDate birthDate,
                                final String phoneNumber,
                                final String address) {
    try {
      Objects.requireNonNull(firstName, "First name is required");
      Objects.requireNonNull(lastName, "Last name is required");
      Objects.requireNonNull(email, "Email is required");
      Objects.requireNonNull(birthDate, "Birth date is required");
      return new Customer(firstName, lastName, email, birthDate, phoneNumber, address);
    } catch (final NullPointerException e) {
      throw new CustomerException("Failed to create Customer: " + e.getMessage());
    }
  }

  public boolean isAdult() {
    return LocalDate.now().isAfter(birthDate.plusYears(18));
  }
}
```

### Using the generated Fixture DSL

You can create a `DataSet` class annotated with `@Fixture`.
The annotation processor generates a fluent, chainable builder:

- `buildDefault()` → immediately builds the entity using **all default values** from your `DataModel`.
- `defaultFixture()` → returns a **mutable builder** pre-filled with the `DataModel` defaults; call `build()` to create the entity.
- `with<Field>(value)` → overrides a single field on the underlying `DataModel`.
- `without<Field>()` → convenience for `with<Field>(null)` (sets the model field to `null`).
- All `with…`/`without…` methods are **chainable**; **last call wins**.

> Generated sources live under  
> `build/generated/sources/annotationProcessor/java/(main|test)/...`

### Minimal example

```java
@GenerateFixture(entityClass = Customer.class, dataModelClass = CustomerDataSet.DataModel.class)
public class CustomerDataSet {
  public static Customer build(DataModel m) {
    return Customer.create(m.firstName, m.lastName, m.email, m.birthDate, m.phoneNumber, m.address);
  }
  public static class DataModel {
    public String firstName = "John";
    public String lastName  = "Smith";
    public String email     = "john.smith@corporation.com";
    public LocalDate birthDate = LocalDate.of(1990, 1, 1);
    public String phoneNumber = "+1234567890";
    public String address     = "123 Main St, Anytown, USA";
  }
}
```
#### Build with defaults

```java
// Exactly equivalent:
Customer a = CustomerFixture.buildDefault();
Customer b = CustomerFixture.defaultFixture().build();
```

#### Override selected fields (with…) and chain

```java
Customer c = CustomerFixture
    .defaultFixture()
    .withFirstName("Alice")
    .withLastName("Doe")
    .withPhoneNumber("+33 6 12 34 56 78")
    .build();
```

#### Explicitly null a field (without…)

```java
Customer d = CustomerFixture
.defaultFixture()
.withoutAddress()     // same as .withAddress(null)
.build();
```
If your factory/constructor enforces non-nulls (e.g., email is required), you can assert failures:
```java
assertThrows(CustomerException.class, () ->
    CustomerFixture.defaultFixture().withoutEmail().build()
);
```
#### Combine with… and without… freely (order doesn’t matter; last wins)

```java
Customer e = CustomerFixture
    .defaultFixture()
    .withoutPhoneNumber()
    .withBirthDate(LocalDate.now().minusYears(25))
    .withoutAddress()
    .withAddress("42 Rue de la Paix")     // last setter wins → address is NOT null
    .build();
```

#### Parameterized tests stay clean and intention-revealing

```java
@ParameterizedTest
@MethodSource("validAdultBirthDateProvider")
void qualifies_as_adult(LocalDate birthDate) {
  Customer customer = CustomerFixture.defaultFixture().withBirthDate(birthDate).build();
  assertTrue(customer.isAdult());
}

static Stream<Arguments> validAdultBirthDateProvider() {
  return Stream.of(
      Arguments.of(LocalDate.now().minusYears(18).minusDays(1)),
      Arguments.of(LocalDate.now().minusYears(25))
  );
}
```
---

## Additional resources

- https://refactoring.guru/design-patterns/builder
- https://ardalis.com/improve-tests-with-the-builder-pattern-for-test-data/

## Thanks

Special thanks to [Frédéric Foissey](https://github.com/ffoissey) for the original idea and initial implementation of these modules. The current codebase extends and maintains his initial work.

## Contributing

Issues and PRs are welcome. Please include a minimal reproduction for bugs.

## Notices & License

- License: [Apache-2.0](./LICENSE)
- Notices: see [NOTICE](./NOTICE)

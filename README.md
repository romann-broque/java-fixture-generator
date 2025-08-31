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

## Usage


Assuming you have a `Client` model you want to test:

```java

public class Client {
  private Long id;
  private String name;
  private String email;
  private boolean active;
  private LocalDateTime createdAt;
  // constructors, getters, setters...
}
```

You can create a `DataSet` class annotated with `@Fixture` to define default values and variations:

```java
package org.example.testfixtures.fixtures.client;

import io.github.romannbroque.fixture.annotations.GenerateFixture;

@GenerateFixture(
    entityClass = Client.class,
    dataModelClass = ClientDataSet.DataModel.class
)
public class ClientDataSet {

  public static Client build(final DataModel model) {
    return Client.create(
        model.firstName,
        model.lastName,
        model.phoneNumber,
        model.birthDate
    );
  }

  @NoArgsConstructor
  @AllArgsConstructor
  public static class DataModel {
    public String firstName = "John";
    public String lastName = "Smith";
    public String phoneNumber = "+1234567890";
    public LocalDate birthDate = LocalDate.of(1990, 1, 1);
  }
}
```
---

## Contributing

Issues and PRs are welcome. Please include a minimal reproduction for bugs.

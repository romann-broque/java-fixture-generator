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

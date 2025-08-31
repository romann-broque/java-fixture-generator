package io.github.romannbroque.fixture.processor;

import io.github.romannbroque.fixture.annotations.GenerateFixture;

/**
 * Record holding metadata extracted from the {@link GenerateFixture} annotation.
 *
 * @param entityClassName    Fully qualified name of the target entity class.
 * @param dataModelClassName Fully qualified name of the data model class (inner "Model" class).
 */
record FixtureAnnotation(String entityClassName, String dataModelClassName) {
}

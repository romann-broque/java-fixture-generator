package io.github.romannbroque.fixture.processor;

import io.github.romannbroque.fixture.annotations.GenerateFixture;
import java.util.Map;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

/**
 * Utility class responsible for parsing the {@link GenerateFixture} annotation
 * and extracting relevant metadata from annotated elements.
 */
final class AnnotationParser {

  private final ProcessingEnvironment processingEnv;

  /**
   * Constructs the annotation parser with the provided processing environment.
   *
   * @param processingEnv The processing environment provided by the annotation processor.
   */
  AnnotationParser(final ProcessingEnvironment processingEnv) {
    this.processingEnv = processingEnv;
  }

  /**
   * Parses the {@link GenerateFixture} annotation applied to the given element.
   *
   * @param element The element annotated with {@code GenerateFixture}.
   * @return A {@link FixtureAnnotation} object containing the extracted metadata.
   * @throws IllegalStateException if the annotation is missing or malformed.
   */
  FixtureAnnotation parseGenerateFixtureAnnotation(final TypeElement element) {
    for (final AnnotationMirror mirror : element.getAnnotationMirrors()) {
      if (mirror.getAnnotationType().toString().equals(GenerateFixture.class.getCanonicalName())) {
        final Map<? extends ExecutableElement, ? extends AnnotationValue> values = mirror.getElementValues();
        final String entityClassName = extractClassName(values, "entityClass");
        final String dataModelClassName = extractClassName(values, "dataModelClass");
        return new FixtureAnnotation(entityClassName, dataModelClassName);
      }
    }
    throw new IllegalStateException("@GenerateFixture not found on " + element.getSimpleName());
  }

  /**
   * Extracts a fully qualified class name from the annotation value map.
   *
   * @param values The map of annotation element values.
   * @param key    The name of the annotation attribute to extract.
   * @return The fully qualified class name as a string.
   * @throws IllegalStateException if the key is not present in the annotation.
   */
  private String extractClassName(final Map<? extends ExecutableElement, ? extends AnnotationValue> values,
                                  final String key) {
    for (final var entry : values.entrySet()) {
      if (entry.getKey().getSimpleName().toString().equals(key)) {
        final AnnotationValue value = entry.getValue();
        final TypeMirror typeMirror = (TypeMirror) value.getValue();
        return typeMirror.toString();
      }
    }
    throw new IllegalStateException("Missing value for '" + key + "' in @GenerateFixture annotation.");
  }
}

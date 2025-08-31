package io.github.romannbroque.fixture.processor;

import com.google.auto.service.AutoService;
import io.github.romannbroque.fixture.annotations.GenerateFixture;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

/**
 * Annotation processor responsible for generating Fixture classes based on the {@link GenerateFixture} annotation.
 * This processor detects annotated classes and delegates the actual class generation to {@link FixtureGenerator}.
 */
@AutoService(Processor.class)
@SupportedAnnotationTypes("io.github.romannbroque.fixture.annotations.GenerateFixture")
@SupportedSourceVersion(SourceVersion.RELEASE_21)
public class FixtureProcessor extends AbstractProcessor {

  private FixtureGenerator fixtureGenerator;

  /**
   * Initializes the processor with the processing environment provided by the compiler.
   *
   * @param processingEnv the environment for facilities like element utilities and file creation
   */
  @Override
  public synchronized void init(final ProcessingEnvironment processingEnv) {
    super.init(processingEnv);
    this.fixtureGenerator = new FixtureGenerator(processingEnv);
  }

  /**
   * Processes each class annotated with {@link GenerateFixture} and triggers fixture generation.
   *
   * @param annotations the set of annotations being processed
   * @param roundEnv    the environment for information about the current processing round
   * @return true indicating that this processor handled the annotations
   */
  @Override
  public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {
    for (final Element element : roundEnv.getElementsAnnotatedWith(GenerateFixture.class)) {
      if (element instanceof final TypeElement typeElement) {
        fixtureGenerator.generateFixture(typeElement);
      }
    }
    return true;
  }
}

package io.github.romannbroque.fixture.processor;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Method;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;

/**
 * This class is responsible for generating fixture classes based on the annotated dataset definitions.
 * It analyzes the base dataset class and its nested data model class, then generates a corresponding fixture builder
 * with a fluent API. It also handles the automatic wiring of optional `buildValid` and `buildInvalid` static methods
 * if they are present in the dataset class.
 */
class FixtureGenerator {

  private final ProcessingEnvironment processingEnv;
  private final AnnotationParser annotationParser;
  private final ModelFieldGenerator fieldGenerator;
  private final StringUtils stringUtils;

  FixtureGenerator(final ProcessingEnvironment processingEnv) {
    this.processingEnv = processingEnv;
    this.stringUtils = new StringUtils();
    this.annotationParser = new AnnotationParser(processingEnv);
    this.fieldGenerator = new ModelFieldGenerator(stringUtils);
  }

  /**
   * Generates the fixture class for a given annotated dataset element.
   *
   * @param baseDataSetElement The annotated dataset element.
   */
  void generateFixture(final TypeElement baseDataSetElement) {
    final FixtureAnnotation parsedAnnotation = annotationParser.parseGenerateFixtureAnnotation(baseDataSetElement);

    final String entityClassName = parsedAnnotation.entityClassName();
    final String dataModelClassName = parsedAnnotation.dataModelClassName();
    final String dataModelSimpleName = stringUtils.getSimpleName(dataModelClassName);
    final String entitySimpleName = stringUtils.getSimpleName(entityClassName);
    final String baseClassName = baseDataSetElement.getSimpleName().toString();
    final String fixtureClassName = entitySimpleName + "Fixture";
    final String packageName = processingEnv.getElementUtils().getPackageOf(baseDataSetElement).toString();
    final String baseClassQualifiedName = processingEnv.getElementUtils().getBinaryName(baseDataSetElement).toString();

    final StringBuilder builder = new StringBuilder();
    generatePackageAndImports(builder, packageName, entityClassName, dataModelClassName, baseClassQualifiedName);
    generateClassDeclaration(builder, fixtureClassName, baseClassName, dataModelSimpleName);
    generateFactoryMethods(builder, fixtureClassName, baseClassName, dataModelSimpleName);
    generateModelAccessors(builder, fixtureClassName, dataModelSimpleName);
    generateBuildMethod(builder, entitySimpleName);
    generateFieldMethods(builder, fixtureClassName, dataModelClassName, dataModelSimpleName, baseDataSetElement);

    builder.append("    public ").append(entitySimpleName).append(" build() {\n");
    builder.append("        return ").append(baseClassName).append(".build(model);\n");
    builder.append("    }\n");

    builder.append("}\n");

    writeClass(packageName + "." + fixtureClassName, builder.toString());
  }

  private void generatePackageAndImports(final StringBuilder builder, final String packageName,
                                         final String entityClassName, final String dataModelClassName,
                                         final String baseClassQualifiedName) {
    builder.append("package ").append(packageName).append(";\n\n");
    builder.append("import ").append(entityClassName).append(";\n");
    builder.append("import ").append(dataModelClassName).append(";\n");

    final String baseClassPackage = baseClassQualifiedName.substring(0, baseClassQualifiedName.lastIndexOf('.'));
    if (!baseClassPackage.equals(packageName)) {
      builder.append("import ").append(baseClassQualifiedName).append(";\n");
    }
    builder.append("\n");
  }

  private void generateClassDeclaration(final StringBuilder builder, final String fixtureClassName,
                                        final String baseClassName, final String dataModelSimpleName) {
    builder.append("/**\n");
    builder.append(" * Auto-generated fixture class to simplify the creation of ")
        .append(fixtureClassName.replace("Fixture", ""))
        .append(" instances.\n");
    builder.append(" * This class uses the builder pattern with a fluent API.\n");
    builder.append(" */\n");
    builder.append("public class ").append(fixtureClassName).append(" {\n\n");

    builder.append("    private ").append(dataModelSimpleName).append(" model = ")
        .append(resolveBuildValidCall(baseClassName, dataModelSimpleName)).append(";\n\n");
  }

  private void generateFactoryMethods(final StringBuilder builder, final String fixtureClassName,
                                      final String baseClassName, final String dataModelSimpleName) {
    builder.append("    /**\n")
        .append("     * Creates a default fixture instance.\n")
        .append("     * @return A new instance of ").append(fixtureClassName).append("\n")
        .append("     */\n")
        .append("    public static ").append(fixtureClassName).append(" defaultFixture() {\n")
        .append("        return new ").append(fixtureClassName).append("();\n")
        .append("    }\n\n");

    builder.append("    /**\n")
        .append("     * Creates a fixture instance with a custom model.\n")
        .append("     * @param customModel The custom model to use.\n")
        .append("     * @return A new instance of ").append(fixtureClassName).append("\n")
        .append("     */\n")
        .append("    public static ").append(fixtureClassName).append(" withModel(final ").append(dataModelSimpleName)
        .append(" customModel) {\n")
        .append("        return new ").append(fixtureClassName).append("().useModel(customModel);\n")
        .append("    }\n\n");

    if (hasMethod(baseClassName, "buildInvalid")) {
      builder.append("    public static ").append(fixtureClassName).append(" invalidFixture() {\n")
          .append("        return new ").append(fixtureClassName).append("().useModel(").append(baseClassName)
          .append(".buildInvalid());\n")
          .append("    }\n\n");
    }
  }

  private void generateModelAccessors(final StringBuilder builder, final String fixtureClassName,
                                      final String dataModelSimpleName) {
    builder.append("    public ").append(fixtureClassName).append(" useModel(final ").append(dataModelSimpleName)
        .append(" customModel) {\n")
        .append("        this.model = customModel;\n")
        .append("        return this;\n")
        .append("    }\n\n");

    builder.append("    public ").append(dataModelSimpleName).append(" getModel() {\n")
        .append("        return this.model;\n")
        .append("    }\n\n");
  }

  private void generateBuildMethod(final StringBuilder builder, final String entitySimpleName) {
    builder.append("    public static ").append(entitySimpleName).append(" buildDefault() {\n")
        .append("        return defaultFixture().build();\n")
        .append("    }\n\n");
  }

  private void generateFieldMethods(final StringBuilder builder, final String fixtureClassName,
                                    final String dataModelClassName, final String dataModelSimpleName,
                                    final TypeElement baseDataSetElement) {
    final TypeElement dataModelTypeElement = findDataModelTypeElement(dataModelClassName);

    if (dataModelTypeElement != null) {
      for (final Element field : dataModelTypeElement.getEnclosedElements()) {
        fieldGenerator.generateFieldMethods(builder, field, fixtureClassName);
      }
    } else {
      for (final Element enclosed : baseDataSetElement.getEnclosedElements()) {
        if (enclosed.getKind() == ElementKind.CLASS && enclosed.getSimpleName().contentEquals(dataModelSimpleName)) {
          for (final Element field : enclosed.getEnclosedElements()) {
            fieldGenerator.generateFieldMethods(builder, field, fixtureClassName);
          }
        }
      }
    }
  }

  private TypeElement findDataModelTypeElement(final String className) {
    return processingEnv.getElementUtils().getTypeElement(className);
  }

  private boolean hasMethod(final String className, final String methodName) {
    try {
      final Class<?> clazz = Class.forName(className);
      final Method method = clazz.getMethod(methodName);
      return method != null;
    } catch (Exception e) {
      return false;
    }
  }

  private String resolveBuildValidCall(final String baseClassName, final String dataModelSimpleName) {
    if (hasMethod(baseClassName, "buildValid")) {
      return baseClassName + ".buildValid()";
    }
    return "new " + dataModelSimpleName + "()";
  }

  private void writeClass(final String qualifiedName, final String content) {
    try {
      final JavaFileObject file = processingEnv.getFiler().createSourceFile(qualifiedName);
      try (Writer writer = file.openWriter()) {
        writer.write(content);
      }
    } catch (final IOException e) {
      throw new RuntimeException("Failed to write class: " + qualifiedName, e);
    }
  }
}

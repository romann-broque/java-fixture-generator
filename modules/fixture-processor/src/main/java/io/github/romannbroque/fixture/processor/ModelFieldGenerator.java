package io.github.romannbroque.fixture.processor;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;

/**
 * Code generator responsible for producing methods related to the fields of the data model.
 *
 * <p>
 * This generator creates fluent setters (withX), nullifying setters (withoutX),
 * and getters (getX) for each field in the data model class.
 */
class ModelFieldGenerator {

  private final StringUtils stringUtils;

  ModelFieldGenerator(final StringUtils stringUtils) {
    this.stringUtils = stringUtils;
  }

  /**
   * Generates with/without/get methods for a given model field.
   *
   * @param builder          The StringBuilder used to write the generated code.
   * @param field            The element representing the field.
   * @param fixtureClassName The name of the generated fixture class.
   */
  void generateFieldMethods(final StringBuilder builder, final Element field, final String fixtureClassName) {
    if (field.getKind() != ElementKind.FIELD) {
      return;
    }

    final String type = field.asType().toString();
    final String name = field.getSimpleName().toString();
    final String cap = stringUtils.capitalize(name);

    // Generate "withX" method
    generateWithMethod(builder, fixtureClassName, type, name, cap);

    // Generate "withoutX" method only if the type is not primitive
    if (!field.asType().getKind().isPrimitive()) {
      generateWithoutMethod(builder, fixtureClassName, name, cap);
    }

    // Generate "getX" method
    generateGetMethod(builder, type, name, cap);
  }

  /**
   * Generates the fluent "withX" method.
   *
   * @param builder          The StringBuilder to write into.
   * @param fixtureClassName The fixture class name.
   * @param type             The type of the field.
   * @param name             The field name.
   * @param cap              The capitalized field name.
   */
  private void generateWithMethod(final StringBuilder builder, final String fixtureClassName,
                                  final String type, final String name, final String cap) {
    builder.append("    public ").append(fixtureClassName).append(" with").append(cap)
        .append("(").append(type).append(" value) {\n");
    builder.append("        model.").append(name).append(" = value;\n");
    builder.append("        return this;\n    }\n\n");
  }

  /**
   * Generates the "withoutX" method, setting the field to null.
   *
   * @param builder          The StringBuilder to write into.
   * @param fixtureClassName The fixture class name.
   * @param name             The field name.
   * @param cap              The capitalized field name.
   */
  private void generateWithoutMethod(final StringBuilder builder, final String fixtureClassName,
                                     final String name, final String cap) {
    builder.append("    public ").append(fixtureClassName).append(" without").append(cap).append("() {\n");
    builder.append("        model.").append(name).append(" = null;\n");
    builder.append("        return this;\n    }\n\n");
  }

  /**
   * Generates the "getX" method to retrieve the field's value.
   *
   * @param builder The StringBuilder to write into.
   * @param type    The field type.
   * @param name    The field name.
   * @param cap     The capitalized field name.
   */
  private void generateGetMethod(final StringBuilder builder, final String type,
                                 final String name, final String cap) {
    builder.append("    public ").append(type).append(" get").append(cap).append("() {\n");
    builder.append("        return model.").append(name).append(";\n");
    builder.append("    }\n\n");
  }
}

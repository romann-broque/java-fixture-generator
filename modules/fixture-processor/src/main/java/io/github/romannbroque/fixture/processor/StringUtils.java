package io.github.romannbroque.fixture.processor;

/**
 * Utility class for string manipulation operations.
 */
class StringUtils {

  /**
   * Capitalizes the first letter of a given string.
   *
   * @param input The input string.
   * @return The string with its first letter capitalized, or the original string if null/empty.
   */
  String capitalize(final String input) {
    if (input == null || input.isEmpty()) {
      return input;
    }
    return Character.toUpperCase(input.charAt(0)) + input.substring(1);
  }

  /**
   * Extracts the simple name from a fully qualified class name.
   *
   * @param fullyQualifiedName The fully qualified class name.
   * @return The simple class name.
   */
  String getSimpleName(final String fullyQualifiedName) {
    final int lastDotIndex = fullyQualifiedName.lastIndexOf('.');
    if (lastDotIndex == -1) {
      return fullyQualifiedName;
    }
    return fullyQualifiedName.substring(lastDotIndex + 1);
  }
}

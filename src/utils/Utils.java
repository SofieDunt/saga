package utils;

import java.util.List;

/**
 * A utility class to validate inputs.
 */
public class Utils {

  /**
   * Ensures the given object is not null.
   *
   * @param object  the object to check
   * @param message the message to throw if the object is null
   * @param <K>     the type of the object
   * @return the non-null object
   * @throws IllegalArgumentException if the given object is null
   */
  public static <K> K ensureNotNull(K object, String message) throws IllegalArgumentException {
    if (object == null) {
      if (message == null) {
        message = "Can't be null!";
      }
      throw new IllegalArgumentException(message);
    } else {
      return object;
    }
  }

  /**
   * Removes all null elements from the given list.
   *
   * @param list the list
   * @param <K>  the type of elements in the list
   * @return the same list with the nulls removed
   * @throws IllegalArgumentException if the given list is null
   */
  public static <K> List<K> removeNulls(List<K> list) throws IllegalArgumentException {
    Utils.ensureNotNull(list, "List can't be null!");
    for (int i = list.size() - 1; i >= 0; i--) {
      if (list.get(i) == null) {
        list.remove(i);
      }
    }
    return list;
  }

  /**
   * Checks if the given string can be translated into a valid positive number - that is, it is a
   * non-empty string made up of only characters that represent numbers. ({@code "0"}, {@code
   * "1.0"}, {@code "-1"}, and {@code "1,000"} are invalid)
   *
   * @param proposedNumber the string to check
   * @return true if a valid positive number, false if null/otherwise
   */
  public static boolean isPositiveStringNumber(String proposedNumber) {
    return isStringNumber(proposedNumber) && Integer.parseInt(proposedNumber) > 0;
  }

  /**
   * Checks if the given string can be translated into a valid number - that is, it is a non-empty
   * string made up of only characters that represent numbers.
   *
   * @param proposedNumber the string to check
   * @return true if a valid number, false if null/otherwise
   */
  public static boolean isStringNumber(String proposedNumber) {
    if (proposedNumber == null || proposedNumber.length() == 0) {
      return false;
    }

    try {
      Integer.parseInt(proposedNumber);
      return true;
    } catch (NumberFormatException e) {
      return false;
    }
  }
}

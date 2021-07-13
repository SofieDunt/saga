package utils;

import java.util.Scanner;

/**
 * A utility class for common processes related to IO.
 */
public class IOUtils {

  /**
   * Returns the next string in the given scanner.
   *
   * @param sc  the scanner
   * @param msg the message to throw if there is no next
   * @return the next string in the scanner
   * @throws IllegalArgumentException if the scanner does not have a next or is null
   */
  public static String tryNext(Scanner sc, String msg) throws IllegalArgumentException {
    Utils.ensureNotNull(sc, "Scanner can't be null");
    if (!sc.hasNext()) {
      throw new IllegalArgumentException(msg);
    } else {
      StringBuilder next = new StringBuilder(sc.next());

      // If the string begins with ", return all content between ""
      if (next.charAt(0) == '"') {
        while (next.charAt(next.length() - 1) != '"') {
          next.append(" ");
          next.append(tryNext(sc, msg));
        }
        // Remove the "s. Note that """" -> ""
        next.deleteCharAt(0);
        next.deleteCharAt(next.length() - 1);
      }
      // Return the next input
      return next.toString();
    }
  }

  /**
   * Returns the next int in the given scanner.
   *
   * @param sc  the scanner
   * @param msg the message to throw if there is no next
   * @return the next int in the scanner
   * @throws IllegalArgumentException if the scanner does not have a next int or is null
   */
  public static int tryNextInt(Scanner sc, String msg) throws IllegalArgumentException {
    Utils.ensureNotNull(sc, "Scanner can't be null");
    if (!sc.hasNextInt()) {
      throw new IllegalArgumentException(msg);
    } else {
      return sc.nextInt();
    }
  }

  /**
   * Gets the number from a choice/decision id, in the format X#, where X may be any character that
   * should be ignored.
   *
   * @param stringId the choice/decision id
   * @param msg      the message to throw if the format is illegal
   * @return the number of the id
   * @throws IllegalArgumentException if the id is of an illegal format or the string id is null
   */
  public static int getNumId(String stringId, String msg) throws IllegalArgumentException {
    Utils.ensureNotNull(stringId, "ID can't be null");
    if (stringId.length() <= 1) {
      throw new IllegalArgumentException(msg);
    } else {
      try {
        return Integer.parseInt(stringId.substring(1));
      } catch (NumberFormatException e) {
        throw new IllegalArgumentException(msg);
      }
    }
  }

  /**
   * Returns the appropriate path separator for the user's system.
   *
   * @return the path separator
   */
  public static char pathSeparator() {
    // \ for Windows
    char pathSeparator = '\\';
    // / for Unix (assumes is Unix if not Windows)
    if (!System.getProperty("os.name").startsWith("Windows")) {
      pathSeparator = '/';
    }
    return pathSeparator;
  }
}

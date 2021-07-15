package model.game.statusUpdate;

import com.response.StatusUpdateResponse;
import java.util.Scanner;
import utils.IOUtils;
import utils.Utils;

/**
 * A creator class for creating instances of {@link StatusUpdate}s.
 */
public class StatusUpdateCreator {

  private static final String ILLEGAL_FORMAT_MESSAGE = "Illegal format - not a valid status update";

  /**
   * Creates a status update from the next information in the given scanner.
   *
   * @param sc the scanner
   * @return the status update represented by the next in the scanner
   * @throws IllegalArgumentException if the next information in the scanner does not represent a
   *                                  decision or if the scanner is null
   */
  public static StatusUpdate importSimple(Scanner sc) throws IllegalArgumentException {
    Utils.ensureNotNull(sc, "Scanner can't be null");
    StatusUpdateTypes type = getType(sc);

    return StatusUpdateCreator.createSimple(type, IOUtils.tryNextInt(sc, ILLEGAL_FORMAT_MESSAGE));
  }

  /**
   * Creates instances of {@link StatusUpdateTypes#ADD} and {@link StatusUpdateTypes#SET} status
   * updates.
   *
   * @param type the type of status update to create
   * @param n    the specification for the update (ex. increment)
   * @return the status update instance
   * @throws IllegalArgumentException if the type is null or unsupported
   */
  public static StatusUpdate createSimple(StatusUpdateTypes type, int n)
      throws IllegalArgumentException {
    Utils.ensureNotNull(type, "Type can't be null!");
    switch (type) {
      case ADD:
        return new AddStatus(n);
      case SET:
        return new SetStatus(n);
      default:
        throw new IllegalArgumentException(type + " is not a simple status update");
    }
  }

  /**
   * Creates a status update response from the next values in the given scanner.
   *
   * @param sc the scanner
   * @return the status update response
   * @throws IllegalArgumentException if the scanner is null or does not have next values, or the
   *                                  next values do not represent a status update
   */
  public static StatusUpdateResponse createResponse(Scanner sc) throws IllegalArgumentException {
    Utils.ensureNotNull(sc, "Scanner can't be null");
    return new StatusUpdateResponse(getType(sc),
        IOUtils.tryNextInt(sc, ILLEGAL_FORMAT_MESSAGE),
        IOUtils.tryNext(sc, ILLEGAL_FORMAT_MESSAGE));
  }

  /**
   * Gets the type of the status update represented by the scanner's next values.
   *
   * @param sc the scanner, assumed not null
   * @return the status update type
   * @throws IllegalArgumentException if the next value is not a status update type
   */
  private static StatusUpdateTypes getType(Scanner sc) throws IllegalArgumentException {
    String stringType = IOUtils.tryNext(sc, ILLEGAL_FORMAT_MESSAGE);
    try {
      return StatusUpdateTypes.valueOf(stringType);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException(ILLEGAL_FORMAT_MESSAGE);
    }
  }
}

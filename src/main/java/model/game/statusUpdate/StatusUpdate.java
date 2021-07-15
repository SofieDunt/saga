package model.game.statusUpdate;

/**
 * An interface for a function object that returns the updated status value of a story given its
 * previous status value.
 */
public interface StatusUpdate {

  /**
   * Calculates the updated status value.
   *
   * @param status the previous status value
   * @return the updated status value
   */
  int update(int status);

  /**
   * Returns the status update as a string of a known format to use in exporting stories.
   *
   * @return the formatted string status update
   */
  String export();
}

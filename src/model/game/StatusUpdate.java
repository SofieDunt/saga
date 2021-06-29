package model.game;

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
}

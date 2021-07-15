package model.game.statusUpdate;

/**
 * Represents a {@link StatusUpdate} that adds to the status by some increment (may be negative).
 */
public class AddStatus implements StatusUpdate {

  private final int increment;

  /**
   * Constructs an {@code AddStatus} that increments by the given number.
   *
   * @param increment the number to increment by
   */
  public AddStatus(int increment) {
    this.increment = increment;
  }

  @Override
  public int update(int status) {
    return this.increment + status;
  }

  @Override
  public String export() {
    return "ADD " + this.increment;
  }
}

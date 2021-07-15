package model.game.statusUpdate;

/**
 * Represents a {@link StatusUpdate} that sets the status to some number (may be negative).
 */
public class SetStatus implements StatusUpdate {

  private final int setTo;

  /**
   * Constructs a {@code SetStatus} that sets the status to the given number.
   *
   * @param setTo the number to set the status to
   */
  public SetStatus(int setTo) {
    this.setTo = setTo;
  }

  @Override
  public int update(int status) {
    return this.setTo;
  }

  @Override
  public String export() {
    return "SET " + this.setTo;
  }
}

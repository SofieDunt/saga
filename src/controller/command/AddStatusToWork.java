package controller.command;

import model.StoryWriterModel;
import utils.Utils;

/**
 * A command object to add a status to the current work of a writer model.
 */
public class AddStatusToWork implements Command<StoryWriterModel<?>> {

  private final String name;
  private final int val;

  /**
   * Constructs the command object to add the status of the given name and initial value to the
   * work.
   *
   * @param name the name of the status
   * @param val  the initial value, as a string representation of an integer
   * @throws IllegalArgumentException if the initial value is not an integer
   */
  public AddStatusToWork(String name, String val) throws IllegalArgumentException {
    if (Utils.isStringNumber(val)) {
      this.name = name;
      this.val = Integer.parseInt(val);
    } else {
      throw new IllegalArgumentException("Value must be an integer");
    }
  }

  @Override
  public void execute(StoryWriterModel<?> model) throws IllegalArgumentException {
    Utils.ensureNotNull(model, "Model can't be null");
    model.addStatus(this.name, this.val);
  }
}

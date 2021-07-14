package controller.command;

import model.StoryWriterModel;
import utils.Utils;

/**
 * A command object to set the initial choice of the current work of a writer model.
 */
public class SetInitialChoice implements Command<StoryWriterModel<?>> {

  private final int idx;

  /**
   * Constructs the command object to set the initial choice of the work to the choice at the given
   * index.
   *
   * @param idx the string integer representing the user-friendly index of the choice
   * @throws IllegalArgumentException if the index is not a positive string integer
   */
  public SetInitialChoice(String idx) throws IllegalArgumentException {
    if (Utils.isPositiveStringNumber(idx)) {
      this.idx = Integer.parseInt(idx) - 1;
    } else {
      throw new IllegalArgumentException("Index must be a positive integer");
    }
  }

  @Override
  public void execute(StoryWriterModel<?> model) throws IllegalArgumentException {
    Utils.ensureNotNull(model, "Model can't be null");
    model.setInitialChoice(this.idx);
  }
}

package controller.command;

import model.StoryWriterModel;
import utils.Utils;

/**
 * A command object to remove a choice from the current work of a writer model.
 */
public class RemoveChoice implements Command<StoryWriterModel<?>> {

  private final int choiceIdx;

  /**
   * Constructs the command object to remove the choice at the given index  from the work.
   *
   * @param choiceIdx the string integer representing the user-friendly index of the choice
   * @throws IllegalArgumentException if the index is not a positive string number
   */
  public RemoveChoice(String choiceIdx) throws IllegalArgumentException {
    if (Utils.isPositiveStringNumber(choiceIdx)) {
      this.choiceIdx = Integer.parseInt(choiceIdx) - 1;
    } else {
      throw new IllegalArgumentException("Index must be a positive integer");
    }
  }

  @Override
  public void execute(StoryWriterModel<?> model) throws IllegalArgumentException {
    Utils.ensureNotNull(model, "Model can't be null");
    model.removeChoice(this.choiceIdx);
  }
}

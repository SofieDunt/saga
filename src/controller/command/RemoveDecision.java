package controller.command;

import model.StoryWriterModel;
import utils.Utils;

/**
 * A command object to remove the decision as an option to a specified choice and, if not used
 * elsewhere, from the current work of the writer model.
 */
public class RemoveDecision implements Command<StoryWriterModel<?>> {

  private final int choiceIdx;
  private final int decisionIdx;

  /**
   * Constructs the command object to remove the decision at the given index from the choice at the
   * given index.
   *
   * @param choiceIdx   the string integer representing the user-friendly index of the choice
   * @param decisionIdx the string integer representing the user-friendly index of the decision
   * @throws IllegalArgumentException if the indices are not positive string numbers
   */
  public RemoveDecision(String choiceIdx, String decisionIdx) throws IllegalArgumentException {
    if (Utils.isPositiveStringNumber(choiceIdx) && Utils.isPositiveStringNumber(decisionIdx)) {
      this.choiceIdx = Integer.parseInt(choiceIdx) - 1;
      this.decisionIdx = Integer.parseInt(decisionIdx) - 1;
    } else {
      throw new IllegalArgumentException("Indices must be positive integers");
    }
  }

  @Override
  public void execute(StoryWriterModel<?> model) throws IllegalArgumentException {
    Utils.ensureNotNull(model, "Model can't be null");
    model.removeDecision(this.choiceIdx, this.decisionIdx);
  }
}

package controller.command;

import model.StoryWriterModel;
import utils.Utils;

/**
 * A command object to add a simple decision as an option to a choice in the current work of a
 * writer model.
 */
public class AddSimpleDecision implements Command<StoryWriterModel<?>> {

  private final String description;
  private final int choiceIdx;
  private final int outcomeIdx;

  /**
   * Constructs the command object to add the described simple decision to the choice at the given
   * index with the outcome at the given index.
   *
   * @param description the decision description
   * @param choiceIdx   the string integer representing the user-friendly index of the choice to add
   *                    the option to
   * @param outcomeIdx  the string integer representing the user-friendly index of the outcome the
   *                    decision leads to
   * @throws IllegalArgumentException if the indexes do not represent positive integers
   */
  public AddSimpleDecision(String description, String choiceIdx, String outcomeIdx) throws IllegalArgumentException {
    if (Utils.isPositiveStringNumber(choiceIdx) && Utils.isPositiveStringNumber(outcomeIdx)) {
      this.description = description;
      this.choiceIdx = Integer.parseInt(choiceIdx) - 1;
      this.outcomeIdx = Integer.parseInt(outcomeIdx) - 1;
    } else {
      throw new IllegalArgumentException("Indices must be positive integers");
    }
  }

  @Override
  public void execute(StoryWriterModel<?> model) throws IllegalArgumentException {
    Utils.ensureNotNull(model, "Model can't be null");
    model.addSimpleDecision(this.description, this.choiceIdx, this.outcomeIdx);
  }
}

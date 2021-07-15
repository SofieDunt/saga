package controller.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import model.StoryWriterModel;
import utils.Utils;

/**
 * A command object to add a consequential option to a choice in the story being written.
 */
public class AddConsequentialDecision implements Command<StoryWriterModel<?>> {

  private final String description;
  private final int choiceIdx;
  private final int outcomeIdx;
  private final List<String> consequences;

  /**
   * Constructs the command object to add the described consequential decision to the choice at the
   * given index with the outcome at the given index.
   *
   * @param description  the decision description
   * @param choiceIdx    the string integer representing the user-friendly index of the choice to
   *                     add the option to
   * @param outcomeIdx   the string integer representing the user-friendly index of the outcome the
   *                     decision leads to
   * @param consequences all the decision consequences in the format they are exported in separated
   *                     by commas
   * @throws IllegalArgumentException if the indexes do not represent positive integers
   */
  public AddConsequentialDecision(String description, String choiceIdx, String outcomeIdx,
      String consequences) throws IllegalArgumentException {
    if (Utils.isPositiveStringNumber(choiceIdx) && Utils.isPositiveStringNumber(outcomeIdx)) {
      this.description = description;
      this.choiceIdx = Integer.parseInt(choiceIdx) - 1;
      this.outcomeIdx = Integer.parseInt(outcomeIdx) - 1;
      if (consequences == null) {
        this.consequences = null;
      } else {
        this.consequences = new ArrayList<>(Arrays.asList(consequences.split(",")));
      }
    } else {
      throw new IllegalArgumentException("Indices must be positive integers");
    }
  }

  @Override
  public void execute(StoryWriterModel<?> model) throws IllegalArgumentException {
    Utils.ensureNotNull(model, "Model can't be null");
    model.addConsequentialDecision(this.description, this.choiceIdx, this.outcomeIdx,
        this.consequences);
  }
}

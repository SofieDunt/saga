package controller.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import model.StoryWriterModel;
import utils.Utils;

/**
 * A command object to add a consequential and two-threshold dependent decision as an option to a
 * choice in the current work of a writer model.
 */
public class AddConsequentialDependentDecision implements Command<StoryWriterModel<?>> {

  private final String description;
  private final int choiceIdx;
  private final String dependency;
  private final int threshold;
  private final int outcomeBelowIdx;
  private final int outcomeMeetsIdx;
  private final List<String> consequences;

  /**
   * Constructs the command object to add the described consequential dependent decision to the
   * choice at the given index with the possible outcomes at the given indices.
   *
   * @param description     the decision description
   * @param choiceIdx       the string integer representing the user-friendly index of the choice to
   *                        add the option to
   * @param dependency      the name of the status the decision outcome is dependent on
   * @param threshold       the threshold that determines the outcome, must be a number
   * @param outcomeBelowIdx the string integer representing the user-friendly index of the outcome
   *                        the decision leads to if the threshold is not met
   * @param outcomeMeetsIdx the string integer representing the user-friendly index of the outcome
   *                        the decision leads to if the threshold is met
   * @param consequences    all the decision consequences in the format they are exported in
   *                        separated by commas
   * @throws IllegalArgumentException if the indexes do not represent positive integers or the
   *                                  threshold is not a string number
   */
  public AddConsequentialDependentDecision(String description, String choiceIdx, String dependency,
      String threshold, String outcomeBelowIdx, String outcomeMeetsIdx, String consequences)
      throws IllegalArgumentException {
    if (Utils.isPositiveStringNumber(choiceIdx)
        && Utils.isStringNumber(threshold)
        && Utils.isPositiveStringNumber(outcomeBelowIdx)
        && Utils.isPositiveStringNumber(outcomeMeetsIdx)) {
      this.description = description;
      this.choiceIdx = Integer.parseInt(choiceIdx) - 1;
      this.dependency = dependency;
      this.threshold = Integer.parseInt(threshold);
      this.outcomeBelowIdx = Integer.parseInt(outcomeBelowIdx) - 1;
      this.outcomeMeetsIdx = Integer.parseInt(outcomeMeetsIdx) - 1;
      if (consequences == null) {
        this.consequences = null;
      } else {
        this.consequences = new ArrayList<>(Arrays.asList(consequences.split(",")));
      }
    } else {
      throw new IllegalArgumentException(
          "Indices must be positive integers and threshold must be an integer");
    }
  }

  @Override
  public void execute(StoryWriterModel<?> model) throws IllegalArgumentException {
    Utils.ensureNotNull(model, "Model can't be null");
    model.addConsequentialThresholdDecision(this.description, this.choiceIdx, this.dependency,
        this.threshold, this.outcomeBelowIdx, this.outcomeMeetsIdx, this.consequences);
  }
}

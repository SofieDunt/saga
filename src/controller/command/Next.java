package controller.command;

import model.StoryPlayerModel;
import utils.Utils;

/**
 * A command object to advance to the make a decision in the loaded story.
 */
public class Next implements StoryPlayerCommand {

  private final int decision;

  /**
   * Constructs the command object to make the given decision in the story.
   *
   * @param decision the string integer representing the user-friendly index of the decision
   * @throws IllegalArgumentException if the given decision does not represent an integer
   */
  public Next(String decision) throws IllegalArgumentException {
    if (Utils.isPositiveStringNumber(decision)) {
      this.decision = Integer.parseInt(decision) - 1;
    } else {
      throw new IllegalArgumentException("Decision must be a non-negative integer");
    }
  }

  @Override
  public void execute(StoryPlayerModel<?> model) throws IllegalArgumentException {
    Utils.ensureNotNull(model, "Model can't be null");
    model.next(decision);
  }
}

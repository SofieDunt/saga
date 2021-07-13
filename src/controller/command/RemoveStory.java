package controller.command;

import model.StoryPlayerModel;
import utils.Utils;

/**
 * A command object to remove a story from the model library.
 */
public class RemoveStory implements StoryPlayerCommand {

  private final String storyName;

  /**
   * Constructs the command object to remove the story of the given name from the model library.
   *
   * @param storyName the name of the story
   */
  public RemoveStory(String storyName) {
    this.storyName = storyName;
  }

  @Override
  public void execute(StoryPlayerModel<?> model) throws IllegalArgumentException {
    Utils.ensureNotNull(model, "Model can't be null");
    model.removeStory(this.storyName);
  }
}

package controller.command;

import model.StoryPlayerModel;
import utils.Utils;

/**
 * A command object to play a story in the model library.
 */
public class PlayStory implements Command<StoryPlayerModel<?>> {

  private final String storyName;

  /**
   * Constructs the command object to play the story of the given name.
   *
   * @param storyName the name of the story to play
   */
  public PlayStory(String storyName) {
    this.storyName = storyName;
  }

  @Override
  public void execute(StoryPlayerModel<?> model) throws IllegalArgumentException {
    Utils.ensureNotNull(model, "Model can't be null");
    model.playStory(this.storyName);
  }
}

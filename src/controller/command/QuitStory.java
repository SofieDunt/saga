package controller.command;

import controller.command.Command;
import model.StoryPlayerModel;
import utils.Utils;

/**
 * A command object to quit the loaded story of the model.
 */
public class QuitStory implements Command<StoryPlayerModel<?>> {

  @Override
  public void execute(StoryPlayerModel<?> model) throws IllegalArgumentException {
    Utils.ensureNotNull(model, "Model can't be null");
    model.quitStory();
  }
}

package controller.command;

import controller.command.Command;
import model.StoryPlayerModel;
import utils.Utils;

/**
 * A command object to restart the model's loaded story.
 */
public class Restart implements Command<StoryPlayerModel<?>> {

  @Override
  public void execute(StoryPlayerModel<?> model) throws IllegalArgumentException {
    Utils.ensureNotNull(model, "Model can't be null!");
    model.restart();
  }
}

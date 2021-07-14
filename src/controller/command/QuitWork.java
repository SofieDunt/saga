package controller.command;

import model.StoryWriterModel;
import utils.Utils;

/**
 * A command object to quit the current work.
 */
public class QuitWork implements Command<StoryWriterModel<?>> {

  @Override
  public void execute(StoryWriterModel<?> model) throws IllegalArgumentException {
    Utils.ensureNotNull(model, "Model can't be null");
    model.quit();
  }
}

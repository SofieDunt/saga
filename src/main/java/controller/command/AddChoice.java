package controller.command;

import model.StoryWriterModel;
import utils.Utils;

/**
 * A command object to add a choice to the story being written.
 */
public class AddChoice implements Command<StoryWriterModel<?>> {

  @Override
  public void execute(StoryWriterModel<?> model) throws IllegalArgumentException {
    Utils.ensureNotNull(model, "Model can't be null");
    model.addChoice();
  }
}

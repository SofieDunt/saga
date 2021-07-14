package controller.command;

import model.StoryWriterModel;
import utils.Utils;

/**
 * A command object to set the name of the current work's story in a writer model.
 */
public class SetStoryName implements Command<StoryWriterModel<?>> {

  private final String name;

  /**
   * Constructs the command object to set the current work's story name to the given name.
   *
   * @param name the name to set to
   */
  public SetStoryName(String name) {
    this.name = name;
  }

  @Override
  public void execute(StoryWriterModel<?> model) throws IllegalArgumentException {
    Utils.ensureNotNull(model, "Model can't be null");
    model.setStoryName(this.name);
  }
}

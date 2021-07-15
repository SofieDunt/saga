package controller.command;

import model.StoryWriterModel;
import utils.Utils;

/**
 * A command object to start a new named work.
 */
public class StartWork implements Command<StoryWriterModel<?>> {

  private final String name;

  /**
   * Constructs the command object to start a work of the given name.
   *
   * @param name the name
   */
  public StartWork(String name) {
    this.name = name;
  }

  @Override
  public void execute(StoryWriterModel<?> model) throws IllegalArgumentException {
    Utils.ensureNotNull(model, "Model can't be null");
    model.start(this.name);
  }
}

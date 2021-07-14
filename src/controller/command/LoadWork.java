package controller.command;

import model.StoryWriterModel;
import utils.Utils;

/**
 * A command object to load a work in the writer model.
 */
public class LoadWork implements Command<StoryWriterModel<?>> {

  private final String name;

  /**
   * Loads a work of the given name.
   *
   * @param name the name of the work
   */
  public LoadWork(String name) {
    this.name = name;
  }

  @Override
  public void execute(StoryWriterModel<?> model) throws IllegalArgumentException {
    Utils.ensureNotNull(model, "Model can't be null");
    model.load(this.name);
  }
}

package controller.command;

import model.StoryWriterModel;
import utils.Utils;

/**
 * A command object to remove a work from the writer library.
 */
public class RemoveWork implements Command<StoryWriterModel<?>> {

  private final String name;

  /**
   * Constructs the command object to remove the work of the given name from the library.
   *
   * @param name the name of the work
   */
  public RemoveWork(String name) {
    this.name = name;
  }

  @Override
  public void execute(StoryWriterModel<?> model) throws IllegalArgumentException {
    Utils.ensureNotNull(model, "Model can't be null");
    model.remove(this.name);
  }
}

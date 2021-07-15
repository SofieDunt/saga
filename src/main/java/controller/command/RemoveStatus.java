package controller.command;

import model.StoryWriterModel;
import utils.Utils;

/**
 * A command object to remove a status from the current work of a writer model.
 */
public class RemoveStatus implements Command<StoryWriterModel<?>> {

  private final String name;

  /**
   * Constructs the command object to remove the status of the given name from the model's current
   * work.
   *
   * @param name the status name
   */
  public RemoveStatus(String name) {
    this.name = name;
  }

  @Override
  public void execute(StoryWriterModel<?> model) throws IllegalArgumentException {
    Utils.ensureNotNull(model, "Model can't be null");
    model.removeStatus(this.name);
  }
}

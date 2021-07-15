package controller.command;

import model.StoryWriterModel;
import utils.Utils;

/**
 * A command object to rename a work in a writer library.
 */
public class RenameWork implements Command<StoryWriterModel<?>> {

  private final String name;
  private final String newName;

  /**
   * Constructs the command object to rename the work of the given name to the given new name.
   *
   * @param name    the current name of the work
   * @param newName the new name
   */
  public RenameWork(String name, String newName) {
    this.name = name;
    this.newName = newName;
  }

  @Override
  public void execute(StoryWriterModel<?> model) throws IllegalArgumentException {
    Utils.ensureNotNull(model, "Model can't be null");
    model.rename(this.name, this.newName);
  }
}

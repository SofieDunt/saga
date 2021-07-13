package controller.command;

/**
 * An interface for function objects that command models.
 *
 * @param <K> the type of the model
 */
public interface Command<K> {

  /**
   * Executes the command on the given model.
   *
   * @param model the model
   * @throws IllegalArgumentException if the model could not execute the command or is null.
   */
  void execute(K model) throws IllegalArgumentException;
}

package controller.command;

import java.io.IOException;

/**
 * An interface for function objects that command models and carry out IO
 * processes, such as importing and exporting.
 *
 * @param <K> the type of the model
 */
public interface IOCommand<K> {

  /**
   * Executes the command on the given model.
   *
   * @param model the model
   * @throws IllegalArgumentException if the model could not execute the given command or is null
   * @throws IOException              if the IO process fails
   */
  void execute(K model) throws IllegalArgumentException, IOException;
}

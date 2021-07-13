package controller.command;

import java.io.IOException;
import model.StoryPlayerModel;

/**
 * An interface for function objects that command {@link model.StoryPlayerModel}s and carry out IO
 * processes, such as importing and exporting.
 *
 * @param <K> the type of stories imported and exported
 */
public interface IOCommand<K> {

  /**
   * Executes the command on the given model.
   *
   * @param model the model
   * @throws IllegalArgumentException if the model could not execute the given command or is null
   * @throws IOException              if the IO process fails
   */
  void execute(StoryPlayerModel<K> model) throws IllegalArgumentException, IOException;
}

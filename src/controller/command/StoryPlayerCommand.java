package controller.command;

import model.StoryPlayerModel;

/**
 * An interface for function objects that command {@link model.StoryPlayerModel}s.
 */
public interface StoryPlayerCommand {

  /**
   * Executes the command on the given model.
   *
   * @param model the model
   * @throws IllegalArgumentException if the model could not execute the command or is null.
   */
  void execute(StoryPlayerModel<?> model) throws IllegalArgumentException;
}

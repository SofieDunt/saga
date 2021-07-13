package controller;

/**
 * An interface for the controller of a story playing application which handles user input to
 * control the model and view accordingly.
 */
public interface StoryPlayerController {

  /**
   * Imports, exports, and plays stories until the user quits the application or has no more input.
   *
   * @throws IllegalStateException if publishing to the view fails
   */
  void play() throws IllegalStateException;

}

package controller;

/**
 * An interface for the controller of an application which handles user input to control a model and
 * view accordingly.
 */
public interface ApplicationController {

  /**
   * Controls the application model according to user input until the user quits the application or
   * has no more input.
   *
   * @throws IllegalStateException if publishing to the view fails
   */
  void play() throws IllegalStateException;

}

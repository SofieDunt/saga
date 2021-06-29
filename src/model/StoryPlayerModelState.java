package model;

import java.util.List;

/**
 * An interface containing methods to retrieve information about the application's state that can
 * not mutate the model.
 */
public interface StoryPlayerModelState {

  /**
   * Gets the name of the loaded story.
   *
   * @return the name of the loaded story, or null if no story is loaded
   */
  String getCurrentStoryName();

  /**
   * Gets the string representation of the current choice to be made.
   *
   * @return the current choice, or null if no story is loaded
   */
  String getCurrentChoice();

  /**
   * Gets the names of all stories in the user's library.
   *
   * @return
   */
  List<String> getAllStoryNames();
}

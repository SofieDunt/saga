package model;

import java.util.List;

/**
 * An interface containing methods to retrieve information about the application's state that can
 * not mutate the model.
 *
 * @param <K> the type of story model representing stories in the library
 */
public interface StoryPlayerModelState<K> {

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
   * @return all story names in the library
   */
  List<String> getAllStoryNames();


  /**
   * Gets a copy of the story of the given name in the library.
   *
   * @param name the name of the story
   * @return a copy of the story
   * @throws IllegalArgumentException if the named story does not exist in the library
   */
  K getStory(String name) throws IllegalArgumentException;
}

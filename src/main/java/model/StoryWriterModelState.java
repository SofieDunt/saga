package model;

import java.util.List;
import model.creator.StoryCreatorState;

/**
 * An interface containing methods to retrieve information about the creator application's state
 * that can not mutate the model.
 */
public interface StoryWriterModelState extends StoryCreatorState {

  /**
   * Gets the name of the loaded work.
   *
   * @return the name of the loaded work, or null if no work is loaded
   */
  String getCurrentWorkName();

  /**
   * Gets the names of all works in the user's library.
   *
   * @return all work names in the library
   */
  List<String> getAllWorkNames();
}

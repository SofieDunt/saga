package io;

import model.game.StoryGame;

/**
 * An interface for function objects that import files representing {@link StoryGame}s in some
 * format from a specified path.
 **/
public interface StoryImporter {

  /**
   * Imports the named story, creating a story game.
   *
   * @param filePath the path where the story file is located
   * @return the imported story
   * @throws IllegalArgumentException if the file can't be found or is of an invalid format
   */
  StoryGame importStory(String filePath) throws IllegalArgumentException;
}

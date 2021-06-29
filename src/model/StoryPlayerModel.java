package model;

import model.game.StoryGame;

/**
 * An interface of a model for a choose-your-own-adventure story application in which a user can
 * load and play a story in their library.
 */
public interface StoryPlayerModel {

  /**
   * Adds the given story to the user's library, renaming it if a story of that name already exists
   * in the library by adding a count in parentheses after the name, ex. "Story (1)".
   *
   * @param story the story to add to the library
   * @throws IllegalArgumentException if the given story is null
   */
  void addStory(StoryGame story) throws IllegalArgumentException;

  /**
   * Loads the story of the given name to play.
   *
   * @param name the name of the story to play
   * @throws IllegalArgumentException if the named story does not exist in the library
   */
  void playStory(String name) throws IllegalArgumentException;

  /**
   * Makes a choice in the currently loaded story.
   *
   * @param decision the index of the decision to make, starting at 0 and corresponding to the
   *                 translated index output of {@link StoryPlayerModelState#getCurrentChoice()}.
   * @throws IllegalArgumentException if the given decision is invalid
   * @throws IllegalStateException    if no story is loaded
   */
  void next(int decision) throws IllegalArgumentException, IllegalStateException;

  /**
   * Quits the given story, saving progress. Does nothing if no story is loaded.
   */
  void quitStory();
}

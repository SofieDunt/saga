package model;

/**
 * An interface of a model for a choose-your-own-adventure story application in which a user can
 * load and play a story in their library.
 *
 * @param <K> the type of story model representing stories in the library
 */
public interface StoryPlayerModel<K> extends StoryPlayerModelState<K> {

  /**
   * Adds the given story to the user's library, renaming it in the library if a story of that name
   * already exists in the library by adding a count in parentheses after the name, ex. "Story (1)".
   * (Note that when renaming, the actual name of the story does not change, but its alias in the
   * library does).
   *
   * @param story the story to add to the library
   * @throws IllegalArgumentException if the given story is null
   */
  void addStory(K story) throws IllegalArgumentException;

  /**
   * Removes the given story from the user's library.
   *
   * @param name the alias name of the story to remove, as it is in the user's library
   * @throws IllegalArgumentException if the alias does not exist in the library
   */
  void removeStory(String name) throws IllegalArgumentException;

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
   * @return true if the decision was made, false if otherwise (the story is complete)
   * @throws IllegalArgumentException if the given decision is invalid
   * @throws IllegalStateException    if no story is loaded
   */
  boolean next(int decision) throws IllegalArgumentException, IllegalStateException;

  /**
   * Quits the given story, saving progress. Does nothing if no story is loaded.
   */
  void quitStory();
}

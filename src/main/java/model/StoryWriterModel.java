package model;

import model.creator.StoryCreator;
import model.game.StoryGame;

/**
 * The interface of a model for an application in which users can "write" (or create) stories and
 * manage several stories, or works, that they are creating. For each method provided by {@link
 * StoryCreator}, throws an {@link IllegalStateException} if there is no work to be continued.
 *
 * @param <K> the type of the stories being created
 */
public interface StoryWriterModel<K> extends StoryCreator<K>, StoryWriterModelState {

  /**
   * Adds a new work to the library from the given story. If the story's name already exists in the
   * library, creates a valid work name, but keeps the same story name.
   *
   * @param story the story to add as a work
   * @throws IllegalArgumentException if the given story is null
   */
  void add(StoryGame story) throws IllegalArgumentException;

  /**
   * Adds a new work to the library to create a story of the given name. If the name already exists
   * in the library, creates a valid name.
   *
   * @param name the name of the story to be created
   */
  void start(String name);

  /**
   * Removes the work of the given name from the library.
   *
   * @param name the name of the work
   * @throws IllegalArgumentException if no work of the given name exists
   */
  void remove(String name) throws IllegalArgumentException;

  /**
   * Renames the work of the given name in the library.
   *
   * @param name    the name of the work
   * @param newName the new name
   * @throws IllegalArgumentException if no work of the given name exists or the new name is
   *                                  invalid
   */
  void rename(String name, String newName) throws IllegalArgumentException;

  /**
   * Loads the work of the given name to work on it.
   *
   * @param name the name of the work
   * @throws IllegalArgumentException if no work of the given name exists
   */
  void load(String name) throws IllegalArgumentException;

  /**
   * Stops working on the current work. Does nothing if no work is loaded.
   */
  void quit();
}

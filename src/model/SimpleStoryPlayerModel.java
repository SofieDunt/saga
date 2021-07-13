package model;

import java.util.List;
import model.game.SimpleStoryGame;
import model.game.StoryGame;
import utils.Library;
import utils.MapLibrary;

/**
 * Represents a model for a choose-your-own-adventure story application which keeps track of a
 * user's stories and allows them to select and play any story in their library and add stories to
 * or remove stories from their library.
 */
public class SimpleStoryPlayerModel implements StoryPlayerModel<StoryGame> {

  // the user's library
  private final Library<StoryGame> storyLibrary;
  private String currentStory; // name of the current loaded story from the library, null if none loaded

  /**
   * Constructs a {@code SimpleStoryPlayerModel} with an empty library.
   */
  public SimpleStoryPlayerModel() {
    this.storyLibrary = new MapLibrary<>(s -> "Can't add null", s -> "No story \"" + s + "\" found",
        s -> s + " already exists");
    this.currentStory = null;
  }

  @Override
  public void addStory(StoryGame story) throws IllegalArgumentException {
    this.storyLibrary.add(story.getName(), story);
  }

  @Override
  public void removeStory(String name) throws IllegalArgumentException {
    this.storyLibrary.retrieve(name);
    if (this.currentStory != null && this.currentStory.equals(name)) {
      this.currentStory = null;
    }
    this.storyLibrary.remove(name);
  }

  @Override
  public void playStory(String name) throws IllegalArgumentException {
    this.storyLibrary.retrieve(name);
    this.currentStory = name;
  }

  @Override
  public boolean next(int decision) throws IllegalArgumentException, IllegalStateException {
    ensureStoryLoaded();
    return this.storyLibrary.retrieve(this.currentStory).next(decision);
  }

  @Override
  public void quitStory() {
    this.currentStory = null;
  }

  @Override
  public void restart() {
    ensureStoryLoaded();
    StoryGame original = this.storyLibrary.retrieve(this.currentStory).getOriginalStory();
    this.storyLibrary.update(this.currentStory, original);
  }

  @Override
  public String getCurrentStoryName() {
    return this.currentStory;
  }

  @Override
  public String getCurrentChoice() {
    if (this.currentStory != null) {
      return this.storyLibrary.retrieve(this.currentStory).getCurrentChoice().toString();
    } else {
      return null;
    }
  }

  @Override
  public List<String> getAllStoryNames() {
    return this.storyLibrary.getAllNames();
  }

  @Override
  public StoryGame getStory(String name) throws IllegalArgumentException {
    StoryGame original = this.storyLibrary.retrieve(name);
    return new SimpleStoryGame(original);
  }

  /**
   * Ensures a story is currently loaded.
   *
   * @throws IllegalStateException if no story is loaded
   */
  private void ensureStoryLoaded() throws IllegalStateException {
    if (this.currentStory == null) {
      throw new IllegalStateException("No loaded story!");
    }
  }
}

package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.game.StoryGame;
import utils.Utils;

/**
 * Represents a model for a choose-your-own-adventure story application which keeps track of a
 * user's stories and allows them to select and play any story in their library and add stories to
 * or remove stories from their library.
 */
public class SimpleStoryPlayerModel implements StoryPlayerModel {

  // the name/alias-story map representing the user's library
  private final Map<String, StoryGame> storyLibrary;
  private StoryGame currentStory; // the current loaded story from the library, null if none loaded

  /**
   * Constructs a {@code SimpleStoryPlayerModel} with an empty library.
   */
  public SimpleStoryPlayerModel() {
    this.storyLibrary = new HashMap<>();
    this.currentStory = null;
  }

  @Override
  public void addStory(StoryGame story) throws IllegalArgumentException {
    Utils.ensureNotNull(story, "Can't add null story!");
    this.storyLibrary.put(createValidName(story.getName()), story);
  }

  @Override
  public void removeStory(String name) throws IllegalArgumentException {
    if (name == null || !this.storyLibrary.containsKey(name)) {
      throw new IllegalArgumentException("No story \"" + name + "\" to remove");
    }
    if (this.currentStory != null && this.currentStory.getName().equals(name)) {
      this.currentStory = null;
    }
    this.storyLibrary.remove(name);
  }

  @Override
  public void playStory(String name) throws IllegalArgumentException {
    if (name != null && this.storyLibrary.containsKey(name)) {
      this.currentStory = this.storyLibrary.get(name);
    } else {
      throw new IllegalArgumentException("No story \"" + name + "\" to load");
    }
  }

  @Override
  public boolean next(int decision) throws IllegalArgumentException, IllegalStateException {
    if (this.currentStory != null) {
      return this.currentStory.next(decision);
    } else {
      throw new IllegalStateException("No loaded story!");
    }
  }

  @Override
  public void quitStory() {
    this.currentStory = null;
  }

  @Override
  public String getCurrentStoryName() {
    if (this.currentStory != null) {
      return this.currentStory.getName();
    } else {
      return null;
    }
  }

  @Override
  public String getCurrentChoice() {
    if (this.currentStory != null) {
      return this.currentStory.getCurrentChoice().toString();
    } else {
      return null;
    }
  }

  @Override
  public List<String> getAllStoryNames() {
    List<String> allTitles = new ArrayList<>(this.storyLibrary.keySet());
    Collections.sort(allTitles);
    return allTitles;
  }

  /**
   * Creates a valid name (unique in the story library) from the given name by adding a number after
   * it.
   *
   * @param preferredName the given name
   * @return the valid name
   */
  private String createValidName(String preferredName) {
    if (preferredName == null) {
      preferredName = "Untitled";
    }

    String validName = preferredName;
    int increment = 0;
    while (this.storyLibrary.containsKey(validName)) {
      increment++;
      validName = preferredName + "(" + increment + ")";
    }

    return validName;
  }
}

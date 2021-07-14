package model;

import java.util.List;
import java.util.Map;
import model.creator.StoryCreator;
import model.creator.StoryGameCreator;
import model.game.Choice;
import model.game.StoryGame;
import model.game.decision.Decision;
import utils.Library;
import utils.MapLibrary;

/**
 * Represents a model for a story builder application that keeps track of a user's works and allows
 * them to select and edit any work in their library. Users can also start new works or remove works
 * from their library.
 */
public class SimpleStoryWriterModel implements StoryWriterModel<StoryGame> {

  private final Library<StoryCreator<StoryGame>> workLibrary;
  private String currentWork;

  /**
   * Constructs a {@code SimpleStoryWriterModel} with an empty library.
   */
  public SimpleStoryWriterModel() {
    this.workLibrary = new MapLibrary<>(s -> "Can't add null", s -> "No story \"" + s + "\" found",
        s -> s + " already exists");
    this.currentWork = null;
  }

  @Override
  public void start(String name) {
    this.workLibrary.add(name, new StoryGameCreator(name));
  }

  @Override
  public void remove(String name) throws IllegalArgumentException {
    this.workLibrary.remove(name);
    if (this.currentWork != null && this.currentWork.equals(name)) {
      this.currentWork = null;
    }
  }

  @Override
  public void rename(String name, String newName) throws IllegalArgumentException {
    this.workLibrary.rename(name, newName);
    if (this.currentWork != null && this.currentWork.equals(name)) {
      this.currentWork = newName;
    }
  }

  @Override
  public void load(String name) throws IllegalArgumentException {
    this.workLibrary.retrieve(name);
    this.currentWork = name;
  }

  @Override
  public void quit() {
    this.currentWork = null;
  }

  @Override
  public StoryGame create() throws IllegalStateException {
    return ensureWorkLoaded().create();
  }

  @Override
  public void setStoryName(String name) throws IllegalArgumentException, IllegalStateException {
    ensureWorkLoaded().setStoryName(name);
  }

  @Override
  public void addStatus(String name, int val)
      throws IllegalArgumentException, IllegalStateException {
    ensureWorkLoaded().addStatus(name, val);
  }

  @Override
  public void removeStatus(String name) throws IllegalArgumentException, IllegalStateException {
    ensureWorkLoaded().removeStatus(name);
  }

  @Override
  public int addChoice() throws IllegalStateException {
    return ensureWorkLoaded().addChoice();
  }

  @Override
  public void setInitialChoice(int idx) throws IllegalArgumentException, IllegalStateException {
    ensureWorkLoaded().setInitialChoice(idx);
  }

  @Override
  public void addSimpleDecision(String description, int choiceIdx, int outcomeIdx)
      throws IllegalArgumentException, IllegalStateException {
    ensureWorkLoaded().addSimpleDecision(description, choiceIdx, outcomeIdx);
  }

  @Override
  public void addConsequentialDecision(String description, int choiceIdx, int outcomeIdx,
      List<String> consequences) throws IllegalArgumentException, IllegalStateException {
    ensureWorkLoaded().addConsequentialDecision(description, choiceIdx, outcomeIdx, consequences);
  }

  @Override
  public void addSimpleDependentThresholdDecision(String description, int choiceIdx,
      String dependency, int threshold, int outcomeBelowIdx, int outcomeMeetsIdx)
      throws IllegalArgumentException, IllegalStateException {
    ensureWorkLoaded()
        .addSimpleDependentThresholdDecision(description, choiceIdx, dependency, threshold,
            outcomeBelowIdx, outcomeMeetsIdx);
  }

  @Override
  public void addConsequentialThresholdDecision(String description, int choiceIdx,
      String dependency, int threshold, int outcomeBelowIdx, int outcomeMeetsIdx,
      List<String> consequences) throws IllegalArgumentException, IllegalStateException {
    ensureWorkLoaded()
        .addConsequentialThresholdDecision(description, choiceIdx, dependency, threshold,
            outcomeBelowIdx, outcomeMeetsIdx, consequences);
  }

  @Override
  public void removeDecision(int choiceIdx, int decisionIdx)
      throws IllegalArgumentException, IllegalStateException {
    ensureWorkLoaded().removeDecision(choiceIdx, decisionIdx);
  }

  @Override
  public void removeChoice(int choiceIdx) throws IllegalArgumentException, IllegalStateException {
    ensureWorkLoaded().removeChoice(choiceIdx);
  }

  @Override
  public String getStoryName() {
    if (this.currentWork != null) {
      return ensureWorkLoaded().getStoryName();
    } else {
      return null;
    }
  }

  @Override
  public Map<String, Integer> getStatuses() {
    if (this.currentWork != null) {
      return ensureWorkLoaded().getStatuses();
    } else {
      return null;
    }
  }

  @Override
  public List<Choice> getChoices() {
    if (this.currentWork != null) {
      return ensureWorkLoaded().getChoices();
    } else {
      return null;
    }
  }

  @Override
  public int getInitialChoice() {
    if (this.currentWork != null) {
      return ensureWorkLoaded().getInitialChoice();
    } else {
      return -1234567890;
    }
  }

  @Override
  public List<Decision> getDecisions() {
    if (this.currentWork != null) {
      return ensureWorkLoaded().getDecisions();
    } else {
      return null;
    }
  }

  @Override
  public String getCurrentWorkName() {
    return this.currentWork;
  }

  @Override
  public List<String> getAllWorkNames() {
    return this.workLibrary.getAllNames();
  }

  /**
   * Ensures a work is currently loaded.
   *
   * @return the loaded work
   * @throws IllegalStateException if no work is loaded
   */
  private StoryCreator<StoryGame> ensureWorkLoaded() throws IllegalStateException {
    if (this.currentWork == null) {
      throw new IllegalStateException("No loaded story!");
    } else {
      return this.workLibrary.retrieve(this.currentWork);
    }
  }
}

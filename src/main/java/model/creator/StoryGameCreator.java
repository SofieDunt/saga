package model.creator;

import io.StoryNodes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import model.game.Choice;
import model.game.SimpleChoice;
import model.game.SimpleStoryGame;
import model.game.StoryGame;
import model.game.decision.ConsequentialDecision;
import model.game.decision.Decision;
import model.game.decision.DependentDecision;
import model.game.decision.SimpleDecision;
import model.game.decision.TwoThresholdDeterminer;
import model.game.statusUpdate.StatusUpdate;
import model.game.statusUpdate.StatusUpdateCreator;
import utils.IOUtils;
import utils.Utils;

/**
 * A class that allows users to easily create {@link StoryGame}s.
 */
public class StoryGameCreator implements StoryCreator<StoryGame> {

  private String storyName;
  private final Map<String, Integer> statuses;
  private final List<Choice> choices;
  private int firstChoice;
  private final List<Decision> decisions;
  private final Map<Decision, List<String>> dependencies;
  private final Map<Choice, List<Decision>> options;

  /**
   * Constructs a {@code StoryGameCreator} that creates a story of the given name.
   *
   * @param storyName the name of the story to create
   */
  public StoryGameCreator(String storyName) {
    if (storyName == null) {
      storyName = "Untitled";
    }
    this.storyName = storyName;
    this.statuses = new HashMap<>();
    this.choices = new ArrayList<>();
    this.firstChoice = -1;
    this.decisions = new ArrayList<>();
    this.dependencies = new HashMap<>();
    this.options = new HashMap<>();
  }

  /**
   * Constructs a {@code StoryGameCreator} from the given story.
   *
   * @param story story game
   * @throws IllegalArgumentException if the given story is null
   */
  public StoryGameCreator(StoryGame story) throws IllegalArgumentException {
    Utils.ensureNotNull(story, "Story can't be null");
    StoryNodes nodes = StoryNodes.createNodes(story);
    List<Choice> choices = nodes.getChoices();
    List<Decision> decisions = nodes.getDecisions();
    this.storyName = story.getName();
    this.statuses = story.getStatuses();
    this.choices = choices;
    this.firstChoice = choices.indexOf(story.getCurrentChoice());
    this.decisions = decisions;
    Map<Decision, List<String>> dependencies = new HashMap<>();
    for (Decision decision : decisions) {
      dependencies.put(decision, decision.getDependencies());
    }
    this.dependencies = dependencies;

    Map<Choice, List<Decision>> options = new HashMap<>();
    for (Choice choice : choices) {
      options.put(choice, choice.getOptions());
    }
    this.options = options;
  }

  @Override
  public StoryGame create() {
    return createCurrent();
  }

  @Override
  public void setStoryName(String name) throws IllegalArgumentException {
    Utils.ensureNotNull(name, "Name can't be null");
    this.storyName = name;
  }

  @Override
  public void addStatus(String name, int val) throws IllegalArgumentException {
    Utils.ensureNotNull(name, "Status name can't be null");
    if (this.statuses.containsKey(name)) {
      this.statuses.replace(name, val);
    } else {
      this.statuses.put(name, val);
    }
  }

  @Override
  public void removeStatus(String name) throws IllegalArgumentException {
    Utils.ensureNotNull(name, "Status name can't be null");
    if (this.statuses.containsKey(name) && !isUsedStatus(name)) {
      statuses.remove(name);
    } else if (this.statuses.containsKey(name)) {
      throw new IllegalArgumentException(
          "At least one decision references this status.");
    } else {
      throw new IllegalArgumentException("No status " + name + " in the story");
    }
  }

  @Override
  public int addChoice() {
    List<Decision> options = new ArrayList<>();
    Choice choice = new SimpleChoice(options);
    this.choices.add(choice);
    this.options.put(choice, options);
    return this.choices.size() - 1;
  }

  @Override
  public void setInitialChoice(int idx) throws IllegalArgumentException {
    ensureChoiceExists(idx);
    this.firstChoice = idx;
  }

  @Override
  public void addSimpleDecision(String description, int choiceIdx, int outcomeIdx)
      throws IllegalArgumentException {
    Utils.ensureNotNull(description, "Description can't be null");
    Choice outcome = ensureChoiceExists(outcomeIdx);
    addDecision(new SimpleDecision(description, outcome), choiceIdx);
  }

  @Override
  public void addConsequentialDecision(String description, int choiceIdx, int outcomeIdx,
      List<String> consequences) throws IllegalArgumentException {
    Utils.ensureNotNull(description, "Description can't be null");
    Choice outcome = ensureChoiceExists(outcomeIdx);
    Map<String, StatusUpdate> statusUpdates = getUpdates(consequences);
    if (statusUpdates.size() == 0) {
      addDecision(new SimpleDecision(description, outcome), choiceIdx);
    } else {
      Decision decision = new ConsequentialDecision(description, outcome, statusUpdates);
      addDecision(decision, choiceIdx);
      this.dependencies.get(decision).addAll(statusUpdates.keySet());
    }
  }

  @Override
  public void addSimpleDependentThresholdDecision(String description, int choiceIdx,
      String dependency, int threshold, int outcomeBelowIdx, int outcomeMeetsIdx)
      throws IllegalArgumentException {
    ensureCanMakeDependentDecision(description, dependency);
    Choice below = ensureChoiceExists(outcomeBelowIdx);
    Choice meets = ensureChoiceExists(outcomeMeetsIdx);
    Decision decision = new DependentDecision(description,
        new TwoThresholdDeterminer(dependency, threshold, below, meets));
    addDecision(decision, choiceIdx);
    this.dependencies.get(decision).add(dependency);
  }

  @Override
  public void addConsequentialThresholdDecision(String description, int choiceIdx,
      String dependency, int threshold, int outcomeBelowIdx, int outcomeMeetsIdx,
      List<String> consequences) throws IllegalArgumentException {
    ensureCanMakeDependentDecision(description, dependency);
    Choice below = ensureChoiceExists(outcomeBelowIdx);
    Choice meets = ensureChoiceExists(outcomeMeetsIdx);
    Map<String, StatusUpdate> statusUpdates = getUpdates(consequences);
    if (statusUpdates.size() == 0) {
      addDecision(new DependentDecision(description,
          new TwoThresholdDeterminer(dependency, threshold, below, meets)), choiceIdx);
    } else {
      Decision decision = new DependentDecision(description, statusUpdates,
          new TwoThresholdDeterminer(dependency, threshold, below, meets));
      addDecision(decision, choiceIdx);
      this.dependencies.get(decision).add(dependency);
      this.dependencies.get(decision).addAll(statusUpdates.keySet());
    }
  }

  @Override
  public void removeDecision(int choiceIdx, int decisionIdx) throws IllegalArgumentException {
    Choice choice = ensureChoiceExists(choiceIdx);
    if (decisionIdx >= 0 && decisionIdx < choice.getOptions().size()) {
      Decision decision = choice.getOptions().remove(decisionIdx);
      if (!isAnOption(decision)) { // sanity check but no decision should be used twice
        this.decisions.remove(decision);
        this.dependencies.remove(decision);
      }
    } else {
      throw new IllegalArgumentException("No decision at " + (decisionIdx + 1)); // user-friendly
    }
  }

  @Override
  public void removeChoice(int choiceIdx) throws IllegalArgumentException {
    Choice choice = ensureChoiceExists(choiceIdx);
    if (!isAnOutcome(choice) && this.firstChoice != choiceIdx) {
      this.choices.remove(choice);
      this.options.remove(choice);
    } else {
      throw new IllegalArgumentException(
          "Choice is an outcome of an option or is the first choice");
    }
  }

  @Override
  public String getStoryName() {
    return this.storyName;
  }

  @Override
  public Map<String, Integer> getStatuses() {
    return copyStatuses();
  }

  @Override
  public List<Choice> getChoices() {
    return new ArrayList<>(this.choices);
  }

  @Override
  public int getInitialChoice() {
    return this.firstChoice;
  }

  @Override
  public List<Decision> getDecisions() {
    return new ArrayList<>(this.decisions);
  }

  /**
   * Creates a story game out of the current creator state.
   *
   * @return the story instance
   */
  private StoryGame createCurrent() {
    Choice first;
    if (this.firstChoice == -1) {
      first = SimpleChoice.endChoice();
    } else {
      first = this.choices.get(this.firstChoice);
    }

    return new SimpleStoryGame(this.storyName, first, copyStatuses());
  }

  /**
   * Ensures the choice at the given index exists.
   *
   * @param idx the index, starting at 0
   * @return the choice at the index
   * @throws IllegalArgumentException if the choice does not exist
   */
  private Choice ensureChoiceExists(int idx) throws IllegalArgumentException {
    if (idx >= 0 && idx < this.choices.size()) {
      return this.choices.get(idx);
    } else {
      throw new IllegalArgumentException("No choice at " + (idx + 1)); // user-friendly
    }
  }

  /**
   * Ensures a dependent decision can be made of the given description and dependency.
   *
   * @param description the description
   * @param dependency  the dependency
   * @throws IllegalArgumentException if the dependent decision can't be made
   */
  private void ensureCanMakeDependentDecision(String description, String dependency)
      throws IllegalArgumentException {
    Utils.ensureNotNull(description, "Description can't be null");
    Utils.ensureNotNull(dependency, "Dependency can't be null");
    if (!this.statuses.containsKey(dependency)) {
      throw new IllegalArgumentException("Story has no status: " + dependency);
    }
  }

  /**
   * Returns a copy of the story's statuses.
   *
   * @return the copy
   */
  private Map<String, Integer> copyStatuses() {
    Map<String, Integer> copy = new HashMap<>();
    for (Entry<String, Integer> status : this.statuses.entrySet()) {
      copy.put(status.getKey(), status.getValue());
    }
    return copy;
  }

  /**
   * Adds the given decision as an option of the choice at the given index.
   *
   * @param decision  the decision, assumed not null
   * @param choiceIdx the index of the choice, starting at 0
   * @throws IllegalArgumentException if the given index is invalid
   */
  private void addDecision(Decision decision, int choiceIdx) throws IllegalArgumentException {
    Choice choice = ensureChoiceExists(choiceIdx);
    this.options.get(choice).add(decision);
    this.decisions.add(decision);
    this.dependencies.put(decision, new ArrayList<>());
  }

  /**
   * Creates a map of status updates from the given list of strings representing status updates as
   * they are exported.
   *
   * @param str the list of status updates in string format
   * @return the map of status updates
   * @throws IllegalArgumentException if any of the given strings are of a bad format or the given
   *                                  list is null
   */
  private Map<String, StatusUpdate> getUpdates(List<String> str) throws IllegalArgumentException {
    Map<String, StatusUpdate> statusUpdates = new HashMap<>();
    for (String strUpdate : Utils.removeNulls(str)) {
      Scanner sc = new Scanner(strUpdate);
      StatusUpdate update = StatusUpdateCreator.importSimple(sc);
      String updateName = IOUtils.tryNext(sc, "Invalid status update format");
      if (!statuses.containsKey(updateName)) {
        throw new IllegalArgumentException(updateName + " is not a story status");
      }
      statusUpdates.put(updateName, update);
    }

    return statusUpdates;
  }

  /**
   * Checks whether the given decision is an option of any of the choices.
   *
   * @param decision the decision, assumed not null
   * @return true if it is an option, false if otherwise
   */
  private boolean isAnOption(Decision decision) {
    for (Choice choice : this.choices) {
      for (Decision d : choice.getOptions()) {
        if (d.equals(decision)) {
          return true;
        }
      }
    }

    return false;
  }

  /**
   * Checks whether the given choice is an outcome of any of the decisions.
   *
   * @param choice the choice, assumed not null
   * @return true if it is an outcome, false if otherwise
   */
  private boolean isAnOutcome(Choice choice) {
    for (Decision decision : this.decisions) {
      for (Choice c : decision.getPossibleOutcomes()) {
        if (c.equals(choice)) {
          return true;
        }
      }
    }

    return false;
  }

  /**
   * Checks whether the status of the given name is a dependency or is updated by one of the
   * decisions.
   *
   * @param name the name of the status, assumed not null
   * @return true if it is used, false if otherwise
   */
  private boolean isUsedStatus(String name) {
    for (List<String> dependency : this.dependencies.values()) {
      if (dependency.contains(name)) {
        return true;
      }
    }

    return false;
  }
}

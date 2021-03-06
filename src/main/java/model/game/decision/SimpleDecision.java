package model.game.decision;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import model.game.Choice;
import model.game.StoryGame;
import utils.Utils;

/**
 * Represents a decision made in a story that has no consequences on the status of the story.
 */
public class SimpleDecision implements Decision {

  protected final String description;
  protected final Choice outcome;

  /**
   * Constructs a {@code SimpleDecision} with the given description and outcome.
   *
   * @param description the description of the decision
   * @param outcome     the choice outcome of the decision
   * @throws IllegalArgumentException if either argument is null
   */
  public SimpleDecision(String description, Choice outcome) throws IllegalArgumentException {
    this.description = Utils.ensureNotNull(description, "Description can't be null!");
    this.outcome = Utils.ensureNotNull(outcome, "Outcome can't be null!");
  }

  @Override
  public String toString() {
    return this.description;
  }

  @Override
  public Choice makeDecision(StoryGame story) throws IllegalArgumentException {
    Utils.ensureNotNull(story, "Story can't be null!");
    return this.outcome;
  }

  @Override
  public String export(Map<Choice, String> choiceRepresentations) throws IllegalArgumentException {
    Utils.ensureNotNull(choiceRepresentations, "Map can't be null");
    if (!choiceRepresentations.containsKey(this.outcome)) {
      throw new IllegalArgumentException("Map doesn't contain outcome");
    }

    return DecisionTypes.SIMPLE + " \"" + this.description + "\" " + choiceRepresentations
        .get(this.outcome);
  }

  @Override
  public List<Choice> getPossibleOutcomes() {
    return new ArrayList<>(Collections.singletonList(this.outcome));
  }

  @Override
  public List<String> getDependencies() {
    return new ArrayList<>();
  }
}


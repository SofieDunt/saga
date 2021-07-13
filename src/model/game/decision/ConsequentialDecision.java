package model.game.decision;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import model.game.Choice;
import model.game.statusUpdate.StatusUpdate;
import model.game.StoryGame;
import utils.Utils;

/**
 * Represents a decision made in a choose-your-own-adventure game that may impact at least one
 * status of the story.
 */
public class ConsequentialDecision extends SimpleDecision {

  // A map of the name of the status impacted and the function object that returns the new status
  // based on the old status
  private final Map<String, StatusUpdate> statusUpdates;

  /**
   * Constructs a {@code ConsequentialDecision} of the given description that leads to the given
   * outcome and results in the given consequences.
   *
   * @param description   the decision description
   * @param outcome       the resulting choice of the decision
   * @param statusUpdates the consequences of the decision as a status name-StatusUpdate map
   * @throws IllegalArgumentException if any of the given arguments are null
   */
  public ConsequentialDecision(String description, Choice outcome,
      Map<String, StatusUpdate> statusUpdates) throws IllegalArgumentException {
    super(description, outcome);
    this.statusUpdates = new HashMap<>();
    // Strings and StatusUpdates are immutable
    for (Entry<String, StatusUpdate> entry : statusUpdates.entrySet()) {
      if (entry.getValue() != null) {
        this.statusUpdates.put(entry.getKey(), entry.getValue());
      }
    }
  }

  @Override
  public Choice makeDecision(StoryGame story) throws IllegalArgumentException {
    Utils.ensureNotNull(story, "Story can't be null!");
    Map<String, Integer> statuses = story.getStatuses();
    for (Entry<String, StatusUpdate> update : this.statusUpdates.entrySet()) {
      String toUpdate = update.getKey();
      if (statuses.containsKey(toUpdate)) {
        statuses.replace(toUpdate, update.getValue().update(statuses.get(toUpdate)));
      } else {
        throw new IllegalArgumentException(
            "Can't make decision in given story: no status " + toUpdate);
      }
    }
    return this.outcome;
  }

  @Override
  public String export(Map<Choice, String> choiceRepresentations) {
    Utils.ensureNotNull(choiceRepresentations, "Map can't be null");
    if (!choiceRepresentations.containsKey(this.outcome)) {
      throw new IllegalArgumentException("Map doesn't contain outcome");
    }

    StringBuilder sb = new StringBuilder(DecisionTypes.CONSEQUENTIAL.toString()).append(" \"")
        .append(this.description)
        .append("\" [ ");

    int i = 0;
    Set<String> keys = this.statusUpdates.keySet();
    for (String status : keys) {
      sb.append(this.statusUpdates.get(status).export()).append(" \"").append(status).append("\" ");
      if (keys.size() > 0 && i < keys.size() - 1) {
        sb.append("| ");
      }
      i++;
    }
    sb.append("] ").append(choiceRepresentations.get(this.outcome));
    return sb.toString();
  }
}

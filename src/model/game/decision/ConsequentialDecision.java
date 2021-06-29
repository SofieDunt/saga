package model.game.decision;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import model.game.Choice;
import model.game.StatusUpdate;
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
}

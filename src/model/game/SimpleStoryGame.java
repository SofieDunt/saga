package model.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import utils.Utils;

/**
 * Represents a named choose-your-own story where outcomes may be dependent on the status(es) of the
 * story, which is/are updated as the user makes decisions.
 */
public class SimpleStoryGame implements StoryGame {

  private final String name;
  // a record of all choices made in the story, where the choice at the top of the list is the
  // choice currently being made
  private final List<Choice> choices;
  private final Map<String, Integer> statuses;

  /**
   * Constructs a {@code SimpleStoryGame} of the given name that starts with the given choice and
   * statuses. Ignores statuses whose initial values are null.
   *
   * @param name     the name of the story
   * @param choice   the first choice of the story
   * @param statuses a map containing the names of all statuses of the story to the initial value of
   *                 each status
   */
  public SimpleStoryGame(String name, Choice choice, Map<String, Integer> statuses) {
    this.name = Utils.ensureNotNull(name, "Name can't be null!");
    this.choices = new ArrayList<>(
        Collections.singletonList(Utils.ensureNotNull(choice, "Decision can't be null!")));
    this.statuses = new HashMap<>();
    for (Entry<String, Integer> status : statuses.entrySet()) {
      if (status.getValue() != null) {
        this.statuses.put(status.getKey(), status.getValue());
      }
    }
  }

  @Override
  public String toString() {
    return this.name;
  }

  @Override
  public void next(int decision) throws IllegalArgumentException, IllegalStateException {
    ensureNotFinished();
    this.choices.add(getCurrentChoice().choose(decision, this));
  }

  @Override
  public Choice getCurrentChoice() {
    return this.choices.get(this.choices.size() - 1);
  }

  @Override
  public Map<String, Integer> getStatuses() {
    return this.statuses;
  }

  /**
   * Ensures the story has not yet been finished.
   *
   * @throws IllegalStateException if the story has been finished
   */
  private void ensureNotFinished() throws IllegalStateException {
    if (getCurrentChoice().getOptions().size() == 0) {
      throw new IllegalStateException("The story has been completed!");
    }
  }
}

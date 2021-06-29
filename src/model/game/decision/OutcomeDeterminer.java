package model.game.decision;

import java.util.Map;
import model.game.Choice;

/**
 * An interface for a function object that returns a given choice outcome based on the given story
 * statuses.
 */
public interface OutcomeDeterminer {

  /**
   * Returns the appropriate story outcome based on the given story statuses.
   *
   * @param statuses the story statuses
   * @return the appropriate choice outcome
   * @throws IllegalArgumentException if a required status is not in the map (including if the map
   *                                  is null)
   */
  Choice getOutcome(Map<String, Integer> statuses) throws IllegalArgumentException;
}

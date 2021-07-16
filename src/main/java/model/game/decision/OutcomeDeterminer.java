package model.game.decision;

import java.util.List;
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

  /**
   * Returns the determiner as a string of a known format to use in exporting stories.
   *
   * @param choiceRepresentations a map of choices to how those choices should be represented
   * @return the formatted string determiner
   * @throws IllegalArgumentException if the map is null or does not contain all of the determiner's
   *                                  outcomes
   */
  String export(Map<Choice, String> choiceRepresentations) throws IllegalArgumentException;

  /**
   * Returns a list of all the outcomes the determiner chooses between.
   *
   * @return the list of possible choice outcomes
   */
  List<Choice> getPossibleOutcomes();

  /**
   * Gets the name of the statuses the determiner is dependent on.
   *
   * @return a list of the status names
   */
  List<String> getDependency();
}

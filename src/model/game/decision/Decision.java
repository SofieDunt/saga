package model.game.decision;

import java.util.List;
import java.util.Map;
import model.game.Choice;
import model.game.StoryGame;

/**
 * The interface for a decision made in a story, which provides the method to make the decision.
 */
public interface Decision {

  /**
   * The decision's {@code toString} method returns the description of the decision, which should be
   * informative enough for a user to understand the decision they are making (the action, not
   * necessarily the consequences).
   *
   * @return the description of the decision
   */
  String toString();

  /**
   * Makes the decision, impacting the given story.
   *
   * @param story the story to make the decision in
   * @return the outcome of the decision, i.e. the choice to make after making the decision
   * @throws IllegalArgumentException if the given story is null
   */
  Choice makeDecision(StoryGame story) throws IllegalArgumentException;

  /**
   * Returns the decision as a string of a known format to use in exporting stories.
   *
   * @param choiceRepresentations a map of choices to how those choices should be represented
   * @return the formatted string decision
   * @throws IllegalArgumentException if the map is null or does not contain all of the decision's
   *                                  outcomes
   */
  String export(Map<Choice, String> choiceRepresentations) throws IllegalArgumentException;

  /**
   * Returns a list of all of the decision's possible outcomes.
   * @return the list of choice outcomes
   */
  List<Choice> getPossibleOutcomes();
}

package model.game.decision;

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
}

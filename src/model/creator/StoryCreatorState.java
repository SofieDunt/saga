package model.creator;

import java.util.List;
import java.util.Map;
import model.game.Choice;
import model.game.decision.Decision;

/**
 * An interface containing methods to retrieve information about a story creator's that can not
 * mutate the creator.
 */
public interface StoryCreatorState {

  /**
   * Gets the current name of the story.
   */
  String getStoryName();

  /**
   * Gets the statuses of the story.
   *
   * @return a map of the story statuses (a shallow copy)
   */
  Map<String, Integer> getStatuses();

  /**
   * Gets a list of the choices in the story.
   *
   * @return the list of choices (a shallow copy)
   */
  List<Choice> getChoices();

  /**
   * Gets the initial choice of the story.
   *
   * @return the integer of the initial choice.
   */
  int getInitialChoice();

  /**
   * Gets a list of all decisions in the story.
   *
   * @return the list of decisions (a shallow copy)
   */
  List<Decision> getDecisions();
}

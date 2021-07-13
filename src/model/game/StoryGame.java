package model.game;

import java.util.Map;

/**
 * The interface for a choose-your-own story game which advances as the user gives input, making
 * choices that may affect the story's outcome. A story is composed of choices, though some may be
 * as simple as "choosing" to continue the story. The final choice in a story is a choice with no
 * options. A story may have one or multiple "statuses," which represent some aspect of a game state
 * influenced by the decisions a user has made and which may influence the choices available to the
 * user in the future.
 */
public interface StoryGame {

  /**
   * Advances the story by one choice, making the given decision for the current choice.
   *
   * @param decision the index of the decision to make, starting at 0
   * @return true if the next was made, false if otherwise (the story is complete)
   * @throws IllegalArgumentException if the input is an invalid choice (index)
   */
  boolean next(int decision) throws IllegalArgumentException;

  /**
   * Gets the name of the story.
   *
   * @return the story name
   */
  String getName();

  /**
   * Gets the current choice to be made.
   *
   * @return the current choice
   */
  Choice getCurrentChoice();

  /**
   * Gets the statuses of the game.
   *
   * @return the statuses
   */
  Map<String, Integer> getStatuses();

  /**
   * Returns a copy of the story where no progress has been made. Does not impact the current status
   * of the story.
   *
   * @return a copy of the story, not started
   */
  StoryGame getOriginalStory();
}

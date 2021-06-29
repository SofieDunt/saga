package model.game;

import java.util.List;

/**
 * An interface for a choice to be made in the story, a point where a user must direct the flow of
 * the story by providing some input.
 */
public interface Choice {

  /**
   * The choice's {@code toString} method returns the description of each possible option and the
   * user-friendly index of that option.
   *
   * @return the choice represented by its possible options
   */
  String toString();

  /**
   * Chooses the given decision from the possible choices, influencing the given story.
   *
   * @param decision the index of the decision to make, corresponding to the list returned by {@link
   *                 Choice#getOptions()}, starting at 0
   * @param story    the story to make the decision in
   * @return the choice outcome of the decision made
   * @throws IllegalArgumentException if the given decision does not exist (invalid index) or the
   *                                  story is null
   */
  Choice choose(int decision, StoryGame story) throws IllegalArgumentException;

  /**
   * Returns a list of the descriptions of each decision option for the choice.
   *
   * @return the list of decision descriptions
   */
  List<String> getOptions();
}

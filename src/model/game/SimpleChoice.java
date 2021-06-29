package model.game;

import java.util.ArrayList;
import java.util.List;
import model.game.decision.Decision;
import utils.Utils;

/**
 * Represents a choice that can be made in a story, where a user can make a decision out of specific
 * options.
 */
public class SimpleChoice implements Choice {

  private final List<Decision> options;

  /**
   * Constructs a {@code SimpleChoice} of the given options.
   *
   * @param options the possible decisions
   * @throws IllegalArgumentException if the given list is null
   */
  public SimpleChoice(List<Decision> options) throws IllegalArgumentException {
    this.options = Utils.removeNulls(options);
  }

  /**
   * Returns an instance of a choice that has no options, representing the end of the game (where
   * the user has run out of choices).
   *
   * @return the end game choice
   */
  public static Choice endChoice() {
    return new SimpleChoice(new ArrayList<>());
  }

  @Override
  public String toString() {
    int numOptions = this.options.size();
    if (numOptions == 0) {
      return "Game over, no choices left.";
    }

    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(this.options.get(0).toString()).append(formatChoice(0));
    for (int i = 1; i < numOptions - 1; i++) {
      stringBuilder.append(", ").append(this.options.get(i).toString()).append(formatChoice(i))
          .append(",");
    }
    if (numOptions > 1) {
      stringBuilder.append(" or ").append(this.options.get(numOptions - 1).toString())
          .append(formatChoice(numOptions - 1));
    }
    return stringBuilder.toString();
  }

  @Override
  public Choice choose(int decision, StoryGame story) throws IllegalArgumentException {
    if (decision < 0 || decision > this.options.size() - 1) {
      throw new IllegalArgumentException("No choice " + decision);
    }
    return this.options.get(decision)
        .makeDecision(Utils.ensureNotNull(story, "Story can't be null!"));
  }

  @Override
  public List<String> getOptions() {
    List<String> descriptions = new ArrayList<>();
    for (Decision option : this.options) {
      descriptions.add(option.toString());
    }
    return descriptions;
  }

  /**
   * Returns the given integer index as a user-friendly index enclosed in parentheses. A user
   * friendly-index begins at 1.
   *
   * @param n the integer
   * @return the formatted string
   */
  private static String formatChoice(int n) {
    return "(" + (n + 1) + ")";
  }
}

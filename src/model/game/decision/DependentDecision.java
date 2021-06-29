package model.game.decision;

import java.util.Map;
import model.game.Choice;
import model.game.SimpleChoice;
import model.game.StatusUpdate;
import model.game.StoryGame;
import utils.Utils;

/**
 * Represents a decision made in a choose-your-own-adventure game that may impact at least one
 * status of a story and whose outcome is dependent on the story statuses.
 */
public class DependentDecision implements Decision {

  private final Decision delegate;
  private final OutcomeDeterminer determiner;

  /**
   * Constructs a {@code DependentDecision} that uses the given decision as a delegate and leads to
   * the outcome determined by the given determiner.
   *
   * @param delegate   the delegate decision
   * @param determiner the function object that determines the outcome
   * @throws IllegalArgumentException if any of the arguments are null
   */
  public DependentDecision(Decision delegate, OutcomeDeterminer determiner)
      throws IllegalArgumentException {
    this.delegate = Utils.ensureNotNull(delegate, "Delegate can't be null!");
    this.determiner = Utils.ensureNotNull(determiner, "Determiner can't be null!");
  }

  /**
   * Constructs a {@code DependentDecision} of the given description that leads to the outcome
   * supplied by the given outcome determiner and has no consequences.
   *
   * @param description the decision description
   * @param determiner  the function object that determines the outcome
   * @throws IllegalArgumentException if any of the given arguments are null or the decision has no
   *                                  consequences
   */
  public DependentDecision(String description, OutcomeDeterminer determiner)
      throws IllegalArgumentException {
    this(new SimpleDecision(description, SimpleChoice.endChoice()), determiner);
  }

  /**
   * Constructs a {@code DependentDecision} of the given description that leads to the outcome
   * supplied by the given outcome determiner and results in the given consequences.
   *
   * @param description   the decision description
   * @param statusUpdates the consequences of the decision as a status name-StatusUpdate map
   * @param determiner    the function object that determines the outcome
   * @throws IllegalArgumentException if any of the given arguments are null or the decision has no
   *                                  consequences
   */
  public DependentDecision(String description, Map<String, StatusUpdate> statusUpdates,
      OutcomeDeterminer determiner) throws IllegalArgumentException {
    this(new ConsequentialDecision(description, SimpleChoice.endChoice(), statusUpdates),
        determiner);
  }

  @Override
  public String toString() {
    return this.delegate.toString();
  }

  @Override
  public Choice makeDecision(StoryGame story) throws IllegalArgumentException {
    this.delegate.makeDecision(Utils.ensureNotNull(story, "Story can't be null!"));
    return this.determiner.getOutcome(story.getStatuses());
  }
}

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.game.Choice;
import model.game.SimpleChoice;
import model.game.SimpleStoryGame;
import model.game.StatusUpdate;
import model.game.StoryGame;
import model.game.decision.ConsequentialDecision;
import model.game.decision.Decision;
import model.game.decision.DependentDecision;
import model.game.decision.OutcomeDeterminer;
import model.game.decision.SimpleDecision;

/**
 * A class to provide data instances for testing.
 */
public class TestDataProvider {

  /**
   * Returns a choice between right and left.
   *
   * @return the choice object
   */
  public static Choice twoDirectionChoice() {
    Decision rightDecision = new SimpleDecision("Go right", SimpleChoice.endChoice());
    List<Decision> choices = new ArrayList<>(Collections.singletonList(rightDecision));
    Choice directionChoice = new SimpleChoice(choices);
    Map<String, StatusUpdate> updateLeftCount = new HashMap<>();
    updateLeftCount.put("numLefts", (int i) -> i + 1);
    choices.add(new ConsequentialDecision("Go left", directionChoice, updateLeftCount));

    return directionChoice;
  }

  /**
   * Returns a choice between right, left, and straight.
   *
   * @return the choice object
   */
  public static Choice directionChoice() {
    Decision rightDecision = new SimpleDecision("Go right", SimpleChoice.endChoice());
    List<Decision> choices = new ArrayList<>(Collections.singletonList(rightDecision));
    Choice directionChoice = new SimpleChoice(choices);
    Map<String, StatusUpdate> updateLeftCount = new HashMap<>();
    updateLeftCount.put("numLefts", (int i) -> i + 1);
    choices.add(new ConsequentialDecision("Go left", directionChoice, updateLeftCount));
    Map<String, StatusUpdate> updateStraightCount = new HashMap<>();
    updateStraightCount.put("numStraights", (int i) -> i + 1);
    choices.add(new ConsequentialDecision("Go straight", directionChoice, updateStraightCount));

    return directionChoice;
  }

  /**
   * Returns a story game that does not end until a user goes right and keeps track of the number of
   * times the user goes in other directions.
   *
   * @return the story object
   */
  public static StoryGame goRight() {
    Map<String, Integer> statuses = new HashMap<>();
    statuses.put("numLefts", 0);
    statuses.put("numStraights", 0);
    return new SimpleStoryGame("Go Right!", directionChoice(), statuses);
  }

  /**
   * Represents a story game that uses dependent decisions, which ends if a user chooses "strength"
   * or later quits.
   *
   * @return the story object
   */
  public static StoryGame strengthStory() {
    Map<String, StatusUpdate> updateMap = new HashMap<>();
    List<Decision> choices = new ArrayList<>();
    Choice getStrengthChoice = new SimpleChoice(choices);
    updateMap.put("strength", (i) -> i + 1);
    choices.add(new DependentDecision("get strength", updateMap, getDeterminer()));
    choices.add(new DependentDecision("don't get strength", getDeterminer()));

    Map<String, Integer> statuses = new HashMap<>();
    statuses.put("strength", -1);
    return new SimpleStoryGame("Strength!", getStrengthChoice, statuses);
  }

  /**
   * Returns an {@link OutcomeDeterminer} that returns one of two outcomes based on a story's
   * "strength" status.
   *
   * @return the determiner object
   */
  public static OutcomeDeterminer getDeterminer() {
    return statuses -> {
      if (statuses.containsKey("strength")) {
        if (statuses.get("strength") > 0) {
          return SimpleChoice.endChoice();
        } else {
          List<Decision> choices = new ArrayList<>();
          Choice quitOrContinue = new SimpleChoice(choices);
          Map<String, StatusUpdate> addStrength = new HashMap<>();
          addStrength.put("strength", (i) -> i + 1);
          choices.add(new DependentDecision(
              new ConsequentialDecision("continue", quitOrContinue, addStrength),
              getDeterminer()));
          choices.add(new SimpleDecision("quit", SimpleChoice.endChoice()));
          return quitOrContinue;
        }
      } else {
        throw new IllegalArgumentException("No such status strength");
      }
    };
  }
}

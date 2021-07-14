import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.game.Choice;
import model.game.SimpleChoice;
import model.game.SimpleStoryGame;
import model.game.StoryGame;
import model.game.decision.ConsequentialDecision;
import model.game.decision.Decision;
import model.game.decision.DependentDecision;
import model.game.decision.OutcomeDeterminer;
import model.game.decision.SimpleDecision;
import model.game.decision.TwoThresholdDeterminer;
import model.game.statusUpdate.AddStatus;
import model.game.statusUpdate.StatusUpdate;

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
    updateLeftCount.put("numLefts", new AddStatus(1));
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
    updateLeftCount.put("numLefts", new AddStatus(1));
    choices.add(new ConsequentialDecision("Go left", directionChoice, updateLeftCount));
    Map<String, StatusUpdate> updateStraightCount = new HashMap<>();
    updateStraightCount.put("numStraights", new AddStatus(1));
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
   * Represents a story game that uses dependent decisions, whose ending is dependent on a user's
   * strength, which is determined by their choice.
   *
   * @return the story object
   */
  public static StoryGame strengthStory() {
    OutcomeDeterminer determiner = endDeterminer();
    List<Decision> options = new ArrayList<>();
    Choice getStrengthChoice = new SimpleChoice(options);
    Map<String, StatusUpdate> updateMap = new HashMap<>();
    updateMap.put("strength", new AddStatus(1));
    options.add(new DependentDecision("get 1 strength", updateMap, determiner));
    Map<String, StatusUpdate> updateMap2 = new HashMap<>();
    updateMap2.put("strength", new AddStatus(2));
    options.add(new DependentDecision("get 2 strength", updateMap2, determiner));
    Map<String, StatusUpdate> updateMap3 = new HashMap<>();
    updateMap3.put("strength", new AddStatus(2));
    options.add(new DependentDecision("get 3 strength", updateMap3, determiner));
    options.add(new DependentDecision("don't get strength", determiner));

    Map<String, Integer> statuses = new HashMap<>();
    statuses.put("strength", 0);
    return new SimpleStoryGame("Strength!", getStrengthChoice, statuses);
  }

  /**
   * Returns an {@link OutcomeDeterminer} that returns one of two outcomes based on a story's
   * "strength" status.
   *
   * @return the determiner object
   */
  public static OutcomeDeterminer endDeterminer() {
    Choice win = new SimpleChoice(new ArrayList<>(
        Collections.singletonList(new SimpleDecision("win", SimpleChoice.endChoice()))));
    Choice lose = new SimpleChoice(new ArrayList<>(
        Collections.singletonList(new SimpleDecision("lose", SimpleChoice.endChoice()))));

    return new TwoThresholdDeterminer("strength", 2, win, lose);
  }
}

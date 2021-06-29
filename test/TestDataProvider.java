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

public class TestDataProvider {

  public static Choice twoDirectionChoice() {
    Decision rightDecision = new SimpleDecision("Go right", SimpleChoice.endChoice());
    List<Decision> choices = new ArrayList<>(Collections.singletonList(rightDecision));
    Choice directionChoice = new SimpleChoice(choices);
    Map<String, StatusUpdate> updateLeftCount = new HashMap<>();
    updateLeftCount.put("numLefts", (int i) -> i + 1);
    choices.add(new ConsequentialDecision("Go left", directionChoice, updateLeftCount));

    return directionChoice;
  }

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

  public static StoryGame goRight() {
    Map<String, Integer> statuses = new HashMap<>();
    statuses.put("numLefts", 0);
    statuses.put("numStraights", 0);
    return new SimpleStoryGame("Go Right!", directionChoice(), statuses);
  }

  public static StoryGame strengthStory() {
    Map<String, StatusUpdate> updateMap = new HashMap<>();
    List<Decision> choices = new ArrayList<>();
    Choice getStrengthChoice = new SimpleChoice(choices);
    updateMap.put("strength", (i) -> i + 1);
    choices.add(new DependentDecision("get strength", updateMap, getDeterminer()));
    choices.add(new DependentDecision("don't get strength", getDeterminer()));

    Map<String, Integer> statuses = new HashMap<>();
    statuses.put("strength", 0);
    return new SimpleStoryGame("Strength!", getStrengthChoice, statuses);
  }

  public static OutcomeDeterminer getDeterminer() {
    return statuses -> {
      if (statuses.containsKey("strength")) {
        if (statuses.get("strength") > 0) {
          return SimpleChoice.endChoice();
        } else {
          List<Decision> choices = new ArrayList<>();
          Choice quitOrContinue = new SimpleChoice(choices);
          choices.add(new SimpleDecision("continue", quitOrContinue));
          choices.add(new SimpleDecision("quit", SimpleChoice.endChoice()));
          return quitOrContinue;
        }
      } else {
        throw new IllegalArgumentException("No such status strength");
      }
    };
  }
}

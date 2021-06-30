import static org.junit.Assert.assertEquals;

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
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for {@link Choice}s and {@link Decision}s, testing with different {@link Decision}
 * implementations.
 */
public abstract class ChoiceAndDecisionTests {

  private static StoryGame story;
  protected final Choice toEndChoice = new SimpleChoice(
      new ArrayList<>(Collections.singletonList(endDecision())));
  protected static final Choice endChoice = SimpleChoice.endChoice();

  /**
   * Creates a decision that leads to the end for testing.
   *
   * @return the decision instance
   */
  protected abstract Decision endDecision();

  /**
   * Creates a decision that leads to the toEndChoice for testing.
   *
   * @return the decision instance
   */
  protected abstract Decision decision();

  /**
   * A class for testing {@link SimpleDecision} implementations of {@link Decision}.
   */
  public static class SimpleDecisionTest extends ChoiceAndDecisionTests {

    @Override
    protected Decision endDecision() {
      return new SimpleDecision("End", endChoice);
    }

    @Override
    protected Decision decision() {
      return new SimpleDecision("Decision", toEndChoice);
    }
  }

  /**
   * A class for testing {@link ConsequentialDecision} implementations of {@link Decision}.
   */
  public static class ConsequentialDecisionTest extends ChoiceAndDecisionTests {

    @Override
    protected Decision endDecision() {
      Map<String, StatusUpdate> consequences = new HashMap<>();
      consequences.put("impact1", (i) -> i + 1);
      consequences.put("impact2", (i) -> i + 2);
      return new ConsequentialDecision("End", endChoice, consequences);
    }

    @Override
    protected Decision decision() {
      Map<String, StatusUpdate> consequences = new HashMap<>();
      consequences.put("impact1", (i) -> i - 1);
      consequences.put("impact2", (i) -> i - 2);
      return new ConsequentialDecision("Decision", toEndChoice, consequences);
    }

    @Test
    public void testConsequentialDecisionConstructorStatusUpdates() {
      Map<String, StatusUpdate> consequences = new HashMap<>();
      consequences.put("impact1", (i) -> i - 1);
      consequences.put("impact2", (i) -> i - 2);
      consequences.put("impact3", null);
      Decision decision = new ConsequentialDecision("Decision", toEndChoice, consequences);
      decision.makeDecision(story); // impact3 does not cause an issue because it was removed
      assertEquals(-1, (int) story.getStatuses().get("impact1"));
      assertEquals(98, (int) story.getStatuses().get("impact2"));
      // impact4 does not cause an issue because it constructor makes a copy
      consequences.put("impact4", (i) -> i - 4);
      decision.makeDecision(story);
      assertEquals(-2, (int) story.getStatuses().get("impact1"));
      assertEquals(96, (int) story.getStatuses().get("impact2"));
    }

    @Test
    public void testMakeConsequentialDecision() {
      decision().makeDecision(story);
      assertEquals(-1, (int) story.getStatuses().get("impact1"));
      assertEquals(98, (int) story.getStatuses().get("impact2"));
      endDecision().makeDecision(story);
      assertEquals(0, (int) story.getStatuses().get("impact1"));
      assertEquals(100, (int) story.getStatuses().get("impact2"));
    }

    @Test
    public void testMakeConsequentialDecisionNoStatus() {
      Map<String, StatusUpdate> consequences = new HashMap<>();
      consequences.put("impact1", (i) -> i);
      consequences.put("impact2", (i) -> i);
      consequences.put("impact3", (i) -> i);
      Decision decision = new ConsequentialDecision("Decision", toEndChoice, consequences);

      String msg = "noException";
      try {
        decision.makeDecision(story);
      } catch (IllegalArgumentException e) {
        msg = e.getMessage();
      }
      assertEquals("Can't make decision in given story: no status impact3", msg);
    }
  }

  @Before
  public void initData() {
    Map<String, Integer> statuses = new HashMap<>();
    statuses.put("impact1", 0);
    statuses.put("impact2", 100);
    story = new SimpleStoryGame("Story",
        new SimpleChoice(new ArrayList<>(Collections.singletonList(decision()))), statuses);
  }

  @Test
  public void testMakeDecision() {
    assertEquals(toEndChoice, decision().makeDecision(story));
    assertEquals(endChoice, endDecision().makeDecision(story));
  }

  @Test
  public void makeDependentDecision() {
    OutcomeDeterminer determiner = TestDataProvider.getDeterminer();
    StoryGame storyGame1 = TestDataProvider.strengthStory();

    Map<String, StatusUpdate> updateMap = new HashMap<>();
    updateMap.put("strength", (i) -> i + 1);
    assertEquals("continue(1) or quit(2)",
        new DependentDecision("get strength", updateMap, determiner)
            .makeDecision(storyGame1).toString());
    assertEquals("Game over, no choices left.",
        new DependentDecision("get strength", updateMap, determiner)
            .makeDecision(storyGame1).toString());
    assertEquals("continue(1) or quit(2)",
        new DependentDecision("don't get strength", determiner)
            .makeDecision(TestDataProvider.strengthStory()).toString());

    List<Decision> choices = new ArrayList<>();
    Choice getStrengthChoice = new SimpleChoice(choices);
    updateMap.put("strength", (i) -> i + 1);
    choices.add(new DependentDecision("get strength", updateMap, determiner));
    choices.add(new DependentDecision("don't get strength", determiner));

    Map<String, Integer> statuses = new HashMap<>();
    statuses.put("strength", -5);
    StoryGame storyGame2 = new SimpleStoryGame("Strength!", getStrengthChoice, statuses);
    assertEquals("continue(1) or quit(2)",
        new DependentDecision("get strength", updateMap, determiner)
            .makeDecision(storyGame2).toString());
    storyGame2.next(0);
    for (int i = 0; i < 4; i++) {
      assertEquals("continue(1) or quit(2)", storyGame2.getCurrentChoice().toString());
      storyGame2.next(0);
    }
    assertEquals("Game over, no choices left.", storyGame2.getCurrentChoice().toString());
  }

  // CHOICES

  @Test
  public void choiceToString() {
    assertEquals("End(1)", toEndChoice.toString());
    assertEquals("Go right(1), Go left(2), or Go straight(3)",
        TestDataProvider.directionChoice().toString());
    assertEquals("Go right(1) or Go left(2)", TestDataProvider.twoDirectionChoice().toString());
    assertEquals("Game over, no choices left.", endChoice.toString());
  }

  @Test
  public void makeValidChoice() {
    assertEquals(endChoice, toEndChoice.choose(0, story));
  }

  @Test
  public void makeBadChoice() {
    String msg = "noException";
    try {
      toEndChoice.choose(-1, story);
    } catch (IllegalArgumentException e) {
      msg = e.getMessage();
    }
    assertEquals("No choice -1", msg);
    try {
      toEndChoice.choose(1, story);
    } catch (IllegalArgumentException e) {
      msg = e.getMessage();
    }
    assertEquals("No choice 1", msg);
    try {
      endChoice.choose(0, story);
    } catch (IllegalArgumentException e) {
      msg = e.getMessage();
    }
    assertEquals("No choice 0", msg);
  }
}

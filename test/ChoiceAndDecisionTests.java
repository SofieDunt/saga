import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import model.game.Choice;
import model.game.SimpleChoice;
import model.game.SimpleStoryGame;
import model.game.StoryGame;
import model.game.decision.ConsequentialDecision;
import model.game.decision.Decision;
import model.game.decision.DependentDecision;
import model.game.decision.SimpleDecision;
import model.game.decision.TwoThresholdDeterminer;
import model.game.statusUpdate.AddStatus;
import model.game.statusUpdate.StatusUpdate;
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
  private Choice outcome;
  private Map<Choice, String> choiceMap;
  private Decision simple;
  private Map<String, StatusUpdate> consequences;
  private Decision consequential;

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
      consequences.put("impact1", new AddStatus(1));
      consequences.put("impact2", new AddStatus(2));
      return new ConsequentialDecision("End", endChoice, consequences);
    }

    @Override
    protected Decision decision() {
      Map<String, StatusUpdate> consequences = new HashMap<>();
      consequences.put("impact1", new AddStatus(-1));
      consequences.put("impact2", new AddStatus(-2));
      return new ConsequentialDecision("Decision", toEndChoice, consequences);
    }

    @Test
    public void testConsequentialDecisionConstructorStatusUpdates() {
      Map<String, StatusUpdate> consequences = new HashMap<>();
      consequences.put("impact1", new AddStatus(-1));
      consequences.put("impact2", new AddStatus(-2));
      consequences.put("impact3", null);
      Decision decision = new ConsequentialDecision("Decision", toEndChoice, consequences);
      decision.makeDecision(story); // impact3 does not cause an issue because it was removed
      assertEquals(-1, (int) story.getStatuses().get("impact1"));
      assertEquals(98, (int) story.getStatuses().get("impact2"));
      // impact4 does not cause an issue because it constructor makes a copy
      consequences.put("impact4", new AddStatus(-4));
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
      consequences.put("impact1", new AddStatus(0));
      consequences.put("impact2", new AddStatus(0));
      consequences.put("impact3", new AddStatus(0));
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
    outcome = SimpleChoice.endChoice();
    choiceMap = new HashMap<>();
    simple = new SimpleDecision("test", outcome);
    consequences = new HashMap<>();
    consequential = new ConsequentialDecision("testC", outcome, consequences);
  }

  @Test
  public void testMakeDecision() {
    assertEquals(toEndChoice, decision().makeDecision(story));
    assertEquals(endChoice, endDecision().makeDecision(story));
  }

  @Test
  public void makeDependentDecision() {
    StoryGame storyGame = TestDataProvider.strengthStory();

    assertEquals(
        "get 1 strength(1), get 2 strength(2), get 3 strength(3), or don't get strength(4)",
        storyGame.getCurrentChoice().toString());
    storyGame.next(0);
    assertEquals("win", storyGame.getCurrentChoice().toString());
    storyGame = TestDataProvider.strengthStory();
    storyGame.next(1);
    assertEquals("lose", storyGame.getCurrentChoice().toString());
    storyGame = TestDataProvider.strengthStory();
    storyGame.next(2);
    assertEquals("lose", storyGame.getCurrentChoice().toString());
    storyGame = TestDataProvider.strengthStory();
    storyGame.next(3);
    assertEquals("win", storyGame.getCurrentChoice().toString());
    storyGame.next(0);
    assertEquals("Game over, no choices left.", storyGame.getCurrentChoice().toString());
  }

  // CHOICES

  @Test
  public void choiceToString() {
    assertEquals("End", toEndChoice.toString());
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
    assertEquals("No choice 0", msg);
    try {
      toEndChoice.choose(1, story);
    } catch (IllegalArgumentException e) {
      msg = e.getMessage();
    }
    assertEquals("No choice 2", msg);
    try {
      endChoice.choose(0, story);
    } catch (IllegalArgumentException e) {
      msg = e.getMessage();
    }
    assertEquals("No choice 1", msg);
  }

  // EXPORT DECISION TESTS

  @Test
  public void simpleDecision() {
    String msg = "";
    try {
      simple.export(null);
    } catch (IllegalArgumentException e) {
      msg = e.getMessage();
    }
    assertEquals("Map can't be null", msg);
    try {
      simple.export(choiceMap);
    } catch (IllegalArgumentException e) {
      msg = e.getMessage();
    }
    assertEquals("Map doesn't contain outcome", msg);

    choiceMap.put(outcome, "C0");
    assertEquals("SIMPLE \"test\" C0", simple.export(choiceMap));
  }

  @Test
  public void consequentialDecision() {
    String msg = "";
    try {
      consequential.export(null);
    } catch (IllegalArgumentException e) {
      msg = e.getMessage();
    }
    assertEquals("Map can't be null", msg);
    try {
      consequential.export(choiceMap);
    } catch (IllegalArgumentException e) {
      msg = e.getMessage();
    }
    assertEquals("Map doesn't contain outcome", msg);

    choiceMap.put(outcome, "C0");
    assertEquals("CONSEQUENTIAL \"testC\" [ ] C0", consequential.export(choiceMap));
    consequences.put("point", new AddStatus(2));
    consequential = new ConsequentialDecision("testC", outcome, consequences);
    assertEquals("CONSEQUENTIAL \"testC\" [ ADD 2 \"point\" ] C0", consequential.export(choiceMap));
    consequences.put("point2", new AddStatus(3));
    consequential = new ConsequentialDecision("testC", outcome, consequences);
    assertEquals("CONSEQUENTIAL \"testC\" [ ADD 2 \"point\" | ADD 3 \"point2\" ] C0",
        consequential.export(choiceMap));
  }

  @Test
  public void dependentDecision() {
    Choice outcome2 = SimpleChoice.endChoice();
    Decision dependent = new DependentDecision("dependent",
        new TwoThresholdDeterminer("point", 2, outcome, outcome2));
    choiceMap.put(outcome, "C0");

    String msg = "";
    try {
      dependent.export(null);
    } catch (IllegalArgumentException e) {
      msg = e.getMessage();
    }
    assertEquals("Map can't be null", msg);
    try {
      dependent.export(choiceMap);
    } catch (IllegalArgumentException e) {
      msg = e.getMessage();
    }
    assertEquals("Map doesn't contain all outcomes", msg);

    choiceMap.put(outcome2, "C1");
    assertEquals("DEPENDENT TWOTHRESHOLD \"point\" 2 C0 C1 [ SIMPLE \"dependent\" C0 ]",
        dependent.export(choiceMap));
  }
}

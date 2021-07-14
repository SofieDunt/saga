import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import model.creator.StoryCreator;
import model.creator.StoryGameCreator;
import model.game.Choice;
import model.game.StoryGame;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for {@link StoryGameCreator}s.
 */
public class StoryGameCreatorTest {

  private final StoryCreator<StoryGame> goRightCreator = new StoryGameCreator("Go Right!");

  @Before
  public void createGoRight() {
    goRightCreator.addStatus("numLefts", 0);
    goRightCreator.addStatus("numStraights", 0);
    goRightCreator.addChoice(); // right/left/straight
    goRightCreator.addChoice(); // end
    goRightCreator.setInitialChoice(0);
    goRightCreator.addSimpleDecision("Go right", 0, 1);
    goRightCreator.addConsequentialDecision("Go left", 0, 0,
        Collections.singletonList("ADD 1 numLefts"));
    goRightCreator.addConsequentialDecision("Go straight", 0, 0,
        Collections.singletonList("ADD 1 numStraights"));
  }

  @Test
  public void testConstructor() {
    StoryCreator<StoryGame> creator = new StoryGameCreator(null);
    assertEquals("Untitled", creator.getStoryName());
    assertEquals(0, creator.getDecisions().size());
    assertEquals(0, creator.getChoices().size());
    assertEquals(-1, creator.getInitialChoice());
    assertEquals(0, creator.getStatuses().size());
    StoryGame story = creator.create();
    assertEquals("Game over, no choices left.", story.getCurrentChoice().toString());
  }

  @Test
  public void create() {
    StoryGame story = goRightCreator.create();
    assertEquals("Go right(1), Go left(2), or Go straight(3)", story.getCurrentChoice().toString());
    assertEquals(2, story.getStatuses().size());
    assertEquals(0, (int) story.getStatuses().get("numLefts"));
    assertEquals(0, (int) story.getStatuses().get("numStraights"));
    story.next(1);
    assertEquals(1, (int) story.getStatuses().get("numLefts"));
    assertEquals(0, (int) story.getStatuses().get("numStraights"));
    story.next(2);
    assertEquals(1, (int) story.getStatuses().get("numLefts"));
    assertEquals(1, (int) story.getStatuses().get("numStraights"));
    story.next(0);
    assertEquals(1, (int) story.getStatuses().get("numLefts"));
    assertEquals(1, (int) story.getStatuses().get("numStraights"));
    assertEquals("Game over, no choices left.", story.getCurrentChoice().toString());
  }

  @Test
  public void setStoryName() {
    assertEquals("Go Right!", goRightCreator.getStoryName());
    goRightCreator.setStoryName("name");
    assertEquals("name", goRightCreator.getStoryName());
    StoryGame story = goRightCreator.create();
    assertEquals("name", story.getName());
    assertEquals("Go right(1), Go left(2), or Go straight(3)", story.getCurrentChoice().toString());
    assertEquals(2, story.getStatuses().size());
    assertEquals(0, (int) story.getStatuses().get("numLefts"));
    assertEquals(0, (int) story.getStatuses().get("numStraights"));
    goRightCreator.setStoryName("a long name");
    assertEquals("a long name", goRightCreator.getStoryName());
    story = goRightCreator.create();
    assertEquals("a long name", story.getName());
  }

  @Test(expected = IllegalArgumentException.class)
  public void setStoryNameNull() {
    goRightCreator.setStoryName(null);
  }

  @Test
  public void addStatus() {
    goRightCreator.addStatus("total", 0);
    assertEquals(3, goRightCreator.getStatuses().size());
    assertEquals(0, (int) goRightCreator.getStatuses().get("total"));
    StoryGame story = goRightCreator.create();
    assertEquals(3, story.getStatuses().size());
    assertEquals(0, (int) story.getStatuses().get("total"));
    // Update
    goRightCreator.addStatus("total", 200);
    assertEquals(200, (int) goRightCreator.getStatuses().get("total"));
    story = goRightCreator.create();
    assertEquals(200, (int) story.getStatuses().get("total"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void addStatusNullName() {
    goRightCreator.addStatus(null, 0);
  }

  @Test
  public void removeStatus() {
    goRightCreator.addStatus("total", 2);
    goRightCreator.removeStatus("total");
    assertEquals(2, goRightCreator.getStatuses().size());
    assertFalse(goRightCreator.getStatuses().containsKey("total"));

    goRightCreator.removeDecision(0, 1);

    goRightCreator.removeStatus("numLefts");
    assertEquals(1, goRightCreator.getStatuses().size());
    assertFalse(goRightCreator.getStatuses().containsKey("numLefts"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void removeStatusNullName() {
    goRightCreator.removeStatus(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void removeStatusNoSuch() {
    goRightCreator.removeStatus("noSuchName");
  }

  @Test(expected = IllegalArgumentException.class)
  public void removeStatusConsequence() {
    goRightCreator.removeStatus("numLefts");
  }

  @Test(expected = IllegalArgumentException.class)
  public void removeStatusSimpleDependentDependency() {
    try {
      goRightCreator.addChoice();
      goRightCreator.addStatus("total", 0);
      goRightCreator.addSimpleDependentThresholdDecision("decide", 1, "total", 0, 0, 2);
    } catch (IllegalArgumentException e) {
      // To prove it doesn't happen here
    }
    goRightCreator.removeStatus("total");
  }

  @Test(expected = IllegalArgumentException.class)
  public void removeStatusConsequentialDependentDependency() {
    try {
      goRightCreator.addChoice();
      goRightCreator.addStatus("total", 0);
      goRightCreator.addConsequentialThresholdDecision("decide", 1, "total", 0, 0, 2,
          Collections.singletonList("ADD 1 numLefts"));
    } catch (IllegalArgumentException e) {
      // To prove it doesn't happen here
    }
    goRightCreator.removeStatus("total");
  }

  @Test(expected = IllegalArgumentException.class)
  public void removeStatusConsequentialDependentConsequence() {
    try {
      goRightCreator.addChoice();
      goRightCreator.addStatus("total", 0);
      goRightCreator.addConsequentialThresholdDecision("decide", 1, "numLefts", 0, 0, 2,
          Arrays.asList("ADD 2 numLefts", "SET 10 total"));
    } catch (IllegalArgumentException e) {
      // To prove it doesn't happen here
    }
    goRightCreator.removeStatus("total");
  }

  @Test
  public void addChoice() {
    goRightCreator.addChoice();
    assertEquals(3, goRightCreator.getChoices().size());
  }

  @Test
  public void setFirstChoice() {
    goRightCreator.setInitialChoice(1);
    assertEquals(1, goRightCreator.getInitialChoice());
    StoryGame story = goRightCreator.create();
    assertEquals("Game over, no choices left.", story.getCurrentChoice().toString());
  }

  @Test(expected = IllegalArgumentException.class)
  public void setFirstChoiceInvalidIdxNegative() {
    goRightCreator.setInitialChoice(-1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void setFirstChoiceInvalidIdxAbove() {
    goRightCreator.setInitialChoice(2);
  }

  @Test
  public void addSimpleDecision() {
    goRightCreator.addChoice();
    goRightCreator.addSimpleDecision("End", 1, 2);
    assertEquals(4, goRightCreator.getDecisions().size());
    assertTrue(Arrays.toString(goRightCreator.getDecisions().toArray()).contains("End"));
    StoryGame story = goRightCreator.create();
    story.next(0);
    assertEquals("End", story.getCurrentChoice().toString());
    story.next(0);
    assertEquals("Game over, no choices left.", story.getCurrentChoice().toString());
  }

  @Test(expected = IllegalArgumentException.class)
  public void addSimpleDecisionNull() {
    goRightCreator.addSimpleDecision(null, 0, 1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void addSimpleDecisionInvalidIdxNegativeChoice() {
    goRightCreator.addSimpleDecision("", -1, 1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void addSimpleDecisionInvalidIdxAboveChoice() {
    goRightCreator.addSimpleDecision("", 2, 1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void addSimpleDecisionInvalidIdxNegativeOutcome() {
    goRightCreator.addSimpleDecision("", 0, -1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void addSimpleDecisionInvalidIdxAboveOutcome() {
    goRightCreator.addSimpleDecision("", 0, 2);
  }

  @Test
  public void addConsequentialDecision() {
    goRightCreator.addChoice();
    goRightCreator.addConsequentialDecision("Get lost", 1, 2,
        Arrays.asList("ADD 20 numLefts", "SET 30 numStraights"));
    assertEquals(4, goRightCreator.getDecisions().size());
    assertTrue(Arrays.toString(goRightCreator.getDecisions().toArray()).contains("Get lost"));
    StoryGame story = goRightCreator.create();
    story.next(1);
    story.next(2);
    story.next(0);
    assertEquals("Get lost", story.getCurrentChoice().toString());
    story.next(0);
    assertEquals("Game over, no choices left.", story.getCurrentChoice().toString());
    assertEquals(21, (int) story.getStatuses().get("numLefts"));
    assertEquals(30, (int) story.getStatuses().get("numStraights"));
  }

  @Test
  public void addConsequentialDecisionNullsInList() {
    goRightCreator.addChoice();
    goRightCreator.addConsequentialDecision("Get lost", 1, 2,
        new ArrayList<>(Arrays.asList("ADD 20 numLefts", null, "SET 30 numStraights", null)));
    assertEquals(4, goRightCreator.getDecisions().size());
    assertTrue(Arrays.toString(goRightCreator.getDecisions().toArray()).contains("Get lost"));
    StoryGame story = goRightCreator.create();
    story.next(1);
    story.next(2);
    story.next(0);
    assertEquals("Get lost", story.getCurrentChoice().toString());
    story.next(0);
    assertEquals("Game over, no choices left.", story.getCurrentChoice().toString());
    assertEquals(21, (int) story.getStatuses().get("numLefts"));
    assertEquals(30, (int) story.getStatuses().get("numStraights"));
  }

  @Test
  public void addConsequentialDecisionEmptyConsequences() {
    goRightCreator.addChoice();
    goRightCreator.addConsequentialDecision("Get lost", 1, 2,
        new ArrayList<>());
    assertEquals(4, goRightCreator.getDecisions().size());
    assertTrue(Arrays.toString(goRightCreator.getDecisions().toArray()).contains("Get lost"));
    StoryGame story = goRightCreator.create();
    story.next(1);
    story.next(2);
    story.next(0);
    assertEquals("Get lost", story.getCurrentChoice().toString());

    Map<Choice, String> choiceStringMap = new HashMap<>();
    choiceStringMap.put(story.getCurrentChoice().getOptions().get(0).makeDecision(story), "C");
    assertEquals("SIMPLE \"Get lost\" C",
        story.getCurrentChoice().getOptions().get(0).export(choiceStringMap));
    Map<String, Integer> before = story.getStatuses();
    story.next(0);
    assertEquals("Game over, no choices left.", story.getCurrentChoice().toString());
    assertEquals(before, story.getStatuses());
  }

  @Test
  public void addConsequentialDecisionIgnoreTrailingStatus() {
    goRightCreator.addChoice();
    goRightCreator.addConsequentialDecision("Get lost", 1, 2,
        Arrays.asList("ADD 20 numLefts ignore me", "SET 30 numStraights ignored ignored"));
    assertEquals(4, goRightCreator.getDecisions().size());
    assertTrue(Arrays.toString(goRightCreator.getDecisions().toArray()).contains("Get lost"));
    StoryGame story = goRightCreator.create();
    story.next(1);
    story.next(2);
    story.next(0);
    assertEquals("Get lost", story.getCurrentChoice().toString());
    story.next(0);
    assertEquals("Game over, no choices left.", story.getCurrentChoice().toString());
    assertEquals(21, (int) story.getStatuses().get("numLefts"));
    assertEquals(30, (int) story.getStatuses().get("numStraights"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void addConsequentialDecisionNullDescription() {
    goRightCreator.addConsequentialDecision(null, 0, 1, new ArrayList<>());
  }

  @Test(expected = IllegalArgumentException.class)
  public void addConsequentialDecisionNullList() {
    goRightCreator.addConsequentialDecision("", 0, 1, null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void addConsequentialDecisionInvalidIdxNegativeChoice() {
    goRightCreator.addConsequentialDecision("", -1, 1, new ArrayList<>());
  }

  @Test(expected = IllegalArgumentException.class)
  public void addConsequentialDecisionInvalidIdxAboveChoice() {
    goRightCreator.addConsequentialDecision("", 2, 1, new ArrayList<>());
  }

  @Test(expected = IllegalArgumentException.class)
  public void addConsequentialDecisionInvalidIdxNegativeOutcome() {
    goRightCreator.addConsequentialDecision("", 0, -1, new ArrayList<>());
  }

  @Test(expected = IllegalArgumentException.class)
  public void addConsequentialDecisionInvalidIdxAboveOutcome() {
    goRightCreator.addConsequentialDecision("", 0, 2, new ArrayList<>());
  }

  @Test(expected = IllegalArgumentException.class)
  public void addConsequentialDecisionStatusNotPresent() {
    goRightCreator.addChoice();
    goRightCreator.addConsequentialDecision("Get lost", 1, 2,
        Arrays.asList("ADD 20 numLefts", "SET 20 none"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void addConsequentialDecisionStatusInvalidFormat() {
    goRightCreator.addChoice();
    goRightCreator.addConsequentialDecision("Get lost", 1, 2,
        Arrays.asList("ADD 20", "SET 20 none"));
  }

  @Test
  public void addSimpleDependentThresholdDecision() {
    int threshold = 25;
    goRightCreator.addChoice();
    goRightCreator.addSimpleDependentThresholdDecision("End", 1, "numLefts", threshold, 0, 2);
    assertEquals(4, goRightCreator.getDecisions().size());
    assertTrue(Arrays.toString(goRightCreator.getDecisions().toArray()).contains("End"));
    StoryGame story = goRightCreator.create();
    story.next(0);
    assertEquals("End", story.getCurrentChoice().toString());
    story.next(0);

    for (int i = 0; i < threshold; i++) {
      assertEquals("Go right(1), Go left(2), or Go straight(3)",
          story.getCurrentChoice().toString());
      story.next(1);
      story.next(0);
      assertEquals("End", story.getCurrentChoice().toString());
      story.next(0);
    }

    assertEquals("Game over, no choices left.", story.getCurrentChoice().toString());
  }

  @Test(expected = IllegalArgumentException.class)
  public void addSimpleDependentThresholdDecisionNullDescription() {
    goRightCreator.addSimpleDependentThresholdDecision(null, 0, "numLefts", 2, 0, 1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void addSimpleDependentThresholdDecisionNullDependency() {
    goRightCreator.addSimpleDependentThresholdDecision("", 0, null, 2, 0, 1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void addSimpleDependentThresholdDecisionInvalidIdxNegativeChoice() {
    goRightCreator.addSimpleDependentThresholdDecision("", -1, "numLefts", 2, 0, 1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void addSimpleDependentThresholdDecisionInvalidIdxAboveChoice() {
    goRightCreator.addSimpleDependentThresholdDecision("", 2, "numLefts", 2, 0, 1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void addSimpleDependentThresholdDecisionInvalidIdxNegativeOutcomeBelow() {
    goRightCreator.addSimpleDependentThresholdDecision("", 0, "numLefts", 2, -1, 1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void addSimpleDependentThresholdDecisionInvalidIdxAboveOutcomeBelow() {
    goRightCreator.addSimpleDependentThresholdDecision("", 0, "numLefts", 2, 2, 1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void addSimpleDependentThresholdDecisionInvalidIdxNegativeOutcomeMeets() {
    goRightCreator.addSimpleDependentThresholdDecision("", 0, "numLefts", 2, 0, -1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void addSimpleDependentThresholdDecisionInvalidIdxAboveOutcomeMeets() {
    goRightCreator.addSimpleDependentThresholdDecision("", 0, "numLefts", 2, 0, 2);
  }

  @Test(expected = IllegalArgumentException.class)
  public void addSimpleDependentThresholdDecisionStatusNotPresent() {
    goRightCreator.addSimpleDependentThresholdDecision("", 0, "none", 2, 0, 1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void addSimpleDependentThresholdDecisionStatusNotPresentEmpty() {
    goRightCreator.addSimpleDependentThresholdDecision("", 0, "", 2, 0, 1);
  }

  @Test
  public void addConsequentialThresholdDecision() {
    int threshold = 25;
    goRightCreator.addStatus("number of ends", 0);
    goRightCreator.addChoice();
    goRightCreator.addConsequentialThresholdDecision("End", 1, "numLefts", threshold, 0, 2,
        Collections.singletonList("ADD 1 \"number of ends\""));
    assertEquals(4, goRightCreator.getDecisions().size());
    assertTrue(Arrays.toString(goRightCreator.getDecisions().toArray()).contains("End"));
    StoryGame story = goRightCreator.create();
    story.next(0);
    assertEquals("End", story.getCurrentChoice().toString());
    story.next(0); // +1

    for (int i = 0; i < threshold; i++) {
      assertEquals("Go right(1), Go left(2), or Go straight(3)",
          story.getCurrentChoice().toString());
      story.next(1);
      story.next(0);
      assertEquals("End", story.getCurrentChoice().toString());
      story.next(0); // +2
      assertEquals(i + 2, (int) story.getStatuses().get("number of ends"));
    }

    assertEquals("Game over, no choices left.", story.getCurrentChoice().toString());
  }

  @Test(expected = IllegalArgumentException.class)
  public void addConsequentialThresholdDecisionNullDescription() {
    goRightCreator
        .addConsequentialThresholdDecision(null, 0, "numLefts", 2, 0, 1, new ArrayList<>());
  }

  @Test(expected = IllegalArgumentException.class)
  public void addConsequentialThresholdDecisionNullDependency() {
    goRightCreator.addConsequentialThresholdDecision("", 0, null, 2, 0, 1, new ArrayList<>());
  }

  @Test(expected = IllegalArgumentException.class)
  public void addConsequentialThresholdDecisionNullList() {
    goRightCreator.addConsequentialThresholdDecision("", 0, "numLefts", 2, 0, 1, null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void addConsequentialThresholdDecisionInvalidIdxNegativeChoice() {
    goRightCreator
        .addConsequentialThresholdDecision("", -1, "numLefts", 2, 0, 1, new ArrayList<>());
  }

  @Test(expected = IllegalArgumentException.class)
  public void addConsequentialThresholdDecisionInvalidIdxAboveChoice() {
    goRightCreator.addConsequentialThresholdDecision("", 2, "numLefts", 2, 0, 1, new ArrayList<>());
  }

  @Test(expected = IllegalArgumentException.class)
  public void addConsequentialThresholdDecisionInvalidIdxNegativeOutcomeBelow() {
    goRightCreator
        .addConsequentialThresholdDecision("", 0, "numLefts", 2, -1, 1, new ArrayList<>());
  }

  @Test(expected = IllegalArgumentException.class)
  public void addConsequentialThresholdDecisionInvalidIdxAboveOutcomeBelow() {
    goRightCreator.addConsequentialThresholdDecision("", 0, "numLefts", 2, 2, 1, new ArrayList<>());
  }

  @Test(expected = IllegalArgumentException.class)
  public void addConsequentialThresholdDecisionInvalidIdxNegativeOutcomeMeets() {
    goRightCreator
        .addConsequentialThresholdDecision("", 0, "numLefts", 2, 0, -1, new ArrayList<>());
  }

  @Test(expected = IllegalArgumentException.class)
  public void addConsequentialThresholdDecisionInvalidIdxAboveOutcomeMeets() {
    goRightCreator.addConsequentialThresholdDecision("", 0, "numLefts", 2, 0, 2, new ArrayList<>());
  }

  @Test(expected = IllegalArgumentException.class)
  public void addConsequentialThresholdDecisionStatusNotPresent() {
    goRightCreator.addConsequentialThresholdDecision("", 0, "none", 2, 0, 1, new ArrayList<>());
  }

  @Test(expected = IllegalArgumentException.class)
  public void addConsequentialThresholdDecisionStatusNotPresentEmpty() {
    goRightCreator.addConsequentialThresholdDecision("", 0, "", 2, 0, 1, new ArrayList<>());
  }

  @Test(expected = IllegalArgumentException.class)
  public void addConsequentialThresholdDecisionStatusInvalidFormat() {
    goRightCreator.addChoice();
    goRightCreator.addConsequentialThresholdDecision("Get lost", 0, "numLefts", 2, 0, 1,
        Arrays.asList("ADD 20", "SET 20 none"));
  }

  @Test
  public void removeDecision() {
    goRightCreator.removeDecision(0, 1);
    assertEquals(2, goRightCreator.getDecisions().size());
    assertEquals("[Go right, Go straight]", goRightCreator.getDecisions().toString());
    StoryGame story = goRightCreator.create();
    assertEquals("Go right(1) or Go straight(2)", story.getCurrentChoice().toString());
  }

  @Test(expected = IllegalArgumentException.class)
  public void removeDecisionInvalidIdxNegativeChoice() {
    goRightCreator.removeDecision(-1, 0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void removeDecisionInvalidIdxAboveChoice() {
    goRightCreator.removeDecision(2, 0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void removeDecisionInvalidIdxNegativeDecision() {
    goRightCreator.removeDecision(0, -1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void removeDecisionInvalidIdxAboveDecision() {
    goRightCreator.removeDecision(0, 3);
  }

  @Test
  public void removeChoice() {
    goRightCreator.addChoice();
    goRightCreator.removeChoice(2);
    assertEquals(2, goRightCreator.getChoices().size());
  }

  @Test(expected = IllegalArgumentException.class)
  public void removeChoiceInvalidIdxNegative() {
    goRightCreator.removeChoice(-1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void removeChoiceInvalidIdxAbove() {
    goRightCreator.removeChoice(2);
  }

  @Test(expected = IllegalArgumentException.class)
  public void removeChoiceIsAnOutcome() {
    try {
      goRightCreator.setInitialChoice(1);
    } catch (IllegalArgumentException e) {
      // No exception here
    }
    goRightCreator.removeChoice(0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void removeChoiceIsFirst() {
    try {
      goRightCreator.removeDecision(0, 0);
      goRightCreator.removeDecision(0, 0);
      goRightCreator.removeDecision(0, 0);
    } catch (IllegalArgumentException e) {
      // No exception here
    }
    goRightCreator.removeChoice(0);
  }

  @Test
  public void getStoryName() {
    assertEquals("Go Right!", goRightCreator.getStoryName());
  }

  @Test
  public void getStatuses() {
    assertEquals(2, goRightCreator.getStatuses().size());
    assertEquals(0, (int) goRightCreator.getStatuses().get("numLefts"));
    assertEquals(0, (int) goRightCreator.getStatuses().get("numStraights"));
  }

  @Test
  public void getChoices() {
    assertEquals(2, goRightCreator.getChoices().size());
    assertEquals("[Go right(1), Go left(2), or Go straight(3), Game over, no choices left.]",
        goRightCreator.getChoices().toString());
  }

  @Test
  public void getInitialChoice() {
    assertEquals(0, goRightCreator.getInitialChoice());
  }

  @Test
  public void getDecisions() {
    assertEquals(3, goRightCreator.getDecisions().size());
    assertEquals("[Go right, Go left, Go straight]", goRightCreator.getDecisions().toString());
  }
}

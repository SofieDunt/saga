import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import model.SimpleStoryWriterModel;
import model.StoryWriterModel;
import model.game.StoryGame;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for {@link SimpleStoryWriterModel}s.
 */
public class WriterControllerTest {

  private StoryWriterModel<StoryGame> model;

  @Before
  public void createGoRight() {
    model = new SimpleStoryWriterModel();
    model.start("Go Right!");
    model.load("Go Right!");
    model.addStatus("numLefts", 0);
    model.addStatus("numStraights", 0);
    model.addChoice(); // right/left/straight
    model.addChoice(); // end
    model.setInitialChoice(0);
    model.addSimpleDecision("Go right", 0, 1);
    model.addConsequentialDecision("Go left", 0, 0,
        Collections.singletonList("ADD 1 numLefts"));
    model.addConsequentialDecision("Go straight", 0, 0,
        Collections.singletonList("ADD 1 numStraights"));
    model.quit();
  }

  @Test
  public void testCreateGoRight() {
    model.load("Go Right!");
    StoryGame story = model.create();
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
  public void testConstructor() {
    StoryWriterModel<StoryGame> model1 = new SimpleStoryWriterModel();
    assertEquals(0, model1.getAllWorkNames().size());
    assertNull(model1.getCurrentWorkName());
  }

  @Test
  public void start() {
    model.start("Go Right!");
    assertEquals(Arrays.asList("Go Right!", "Go Right!(1)"), model.getAllWorkNames());
    // When none loaded
    assertNull(model.getCurrentWorkName());
    assertNull(model.getChoices());
    assertNull(model.getDecisions());
    assertEquals(-1234567890, model.getInitialChoice());
    assertNull(model.getStatuses());
    assertNull(model.getStoryName());

    model.load("Go Right!(1)");
    assertEquals("Go Right!(1)", model.getCurrentWorkName());
    // Newly created story
    assertEquals(0, model.getChoices().size());
    assertEquals(0, model.getDecisions().size());
    assertEquals(-1, model.getInitialChoice());
    assertEquals(0, model.getStatuses().size());
    assertEquals("Go Right!", model.getStoryName()); // story kept original name
  }

  @Test
  public void startNull() {
    model.start(null);
    assertEquals(Arrays.asList("Go Right!", "Untitled"), model.getAllWorkNames());
    model.start(null);
    assertEquals(Arrays.asList("Go Right!", "Untitled", "Untitled(1)"), model.getAllWorkNames());
  }

  @Test
  public void remove() {
    model.remove("Go Right!");
    assertEquals(0, model.getAllWorkNames().size());

    // Remove other while working on current
    model.start("a");
    model.start("b");
    model.load("a");
    model.remove("b");
    assertEquals("a", model.getCurrentWorkName());
    assertEquals(1, model.getAllWorkNames().size());
  }

  @Test
  public void removeCurrent() {
    model.load("Go Right!");
    model.remove("Go Right!");
    assertEquals(0, model.getAllWorkNames().size());
    assertNull(model.getCurrentWorkName());
    assertNull(model.getChoices());
    assertNull(model.getDecisions());
    assertEquals(-1234567890, model.getInitialChoice());
    assertNull(model.getStatuses());
    assertNull(model.getStoryName());
  }

  @Test(expected = IllegalArgumentException.class)
  public void removeNoSuch() {
    model.remove("No such");
  }

  @Test
  public void rename() {
    model.rename("Go Right!", "right draft");
    assertEquals(Collections.singletonList("right draft"), model.getAllWorkNames());
    model.rename("right draft", "Right draft");
    assertEquals(Collections.singletonList("Right draft"), model.getAllWorkNames());
  }

  @Test
  public void renameCurrent() {
    model.load("Go Right!");
    model.rename("Go Right!", "Right");
    assertEquals(Collections.singletonList("Right"), model.getAllWorkNames());
    assertEquals("Right", model.getCurrentWorkName());
    assertEquals("Go Right!", model.getStoryName());
  }

  @Test(expected = IllegalArgumentException.class)
  public void renameNoSuch() {
    model.rename("No such", "name");
  }

  @Test(expected = IllegalArgumentException.class)
  public void renameInvalidNewNull() {
    model.rename("Go Right!", null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void renameInvalidNewItself() {
    model.rename("Go Right!", "Go Right!");
  }

  @Test(expected = IllegalArgumentException.class)
  public void renameInvalidNewExists() {
    model.start("Go Right!");
    model.rename("Go Right!", "Go Right!(1)");
  }

  @Test
  public void load() {
    model.load("Go Right!");
    assertEquals(2, model.getChoices().size());
    assertEquals(3, model.getDecisions().size());
    assertEquals(0, model.getInitialChoice());
    assertEquals(2, model.getStatuses().size());
    assertEquals("Go Right!", model.getStoryName());
    model.addSimpleDecision("Test", 0, 1);
    assertEquals(4, model.getDecisions().size());
  }

  @Test(expected = IllegalArgumentException.class)
  public void loadNull() {
    model.load(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void loadNoSuch() {
    model.load("No such");
  }

  @Test
  public void quit() {
    model.quit(); // does nothing
    assertNull(model.getCurrentWorkName());
    assertNull(model.getChoices());
    assertNull(model.getDecisions());
    assertEquals(-1234567890, model.getInitialChoice());
    assertNull(model.getStatuses());
    assertNull(model.getStoryName());
    model.load("Go Right!");
    model.quit();
    assertNull(model.getCurrentWorkName());
    assertNull(model.getChoices());
    assertNull(model.getDecisions());
    assertEquals(-1234567890, model.getInitialChoice());
    assertNull(model.getStatuses());
    assertNull(model.getStoryName());
  }

  @Test
  public void create() {
    model.start("New");
    model.load("Go Right!");
    StoryGame game = model.create();
    assertEquals("Go Right!", game.getName());
    model.setStoryName("go right");
    game = model.create();
    assertEquals("go right", game.getName());
    model.load("New");
    game = model.create();
    assertEquals("New", game.getName());
  }

  @Test(expected = IllegalStateException.class)
  public void createNoneLoaded() {
    model.create();
  }

  @Test
  public void setStoryName() {
    model.load("Go Right!");
    model.setStoryName("right");
    assertEquals("right", model.getStoryName());
    StoryGame story = model.create();
    assertEquals("right", story.getName());
  }

  @Test(expected = IllegalArgumentException.class)
  public void setStoryNameNull() {
    model.load("Go Right!");
    model.setStoryName(null);
  }


  @Test(expected = IllegalStateException.class)
  public void setStoryNameNoneLoaded() {
    model.setStoryName("None");
  }


  @Test
  public void addStatus() {
    model.start("Test");
    model.load("Go Right!");
    model.addStatus("total", 0);
    assertEquals(3, model.getStatuses().size());
    assertEquals(0, (int) model.getStatuses().get("total"));
    StoryGame story = model.create();
    assertEquals(3, story.getStatuses().size());
    assertEquals(0, (int) story.getStatuses().get("total"));
    // Update
    model.addStatus("total", 200);
    assertEquals(200, (int) model.getStatuses().get("total"));
    story = model.create();
    assertEquals(200, (int) story.getStatuses().get("total"));
    model.quit();
    model.load("Test");
    assertEquals(0, model.getStatuses().size());
  }

  @Test(expected = IllegalArgumentException.class)
  public void addStatusNullName() {
    model.load("Go Right!");
    model.addStatus(null, 0);
  }

  @Test(expected = IllegalStateException.class)
  public void addStatusNoneLoaded() {
    model.addStatus("status", 0);
  }

  @Test
  public void removeStatus() {
    model.start("Test");
    model.load("Test");
    model.addStatus("test", 5);
    model.load("Go Right!");
    model.addStatus("total", 2);
    model.removeStatus("total");
    assertEquals(2, model.getStatuses().size());
    assertFalse(model.getStatuses().containsKey("total"));

    model.removeDecision(0, 1);

    model.removeStatus("numLefts");
    assertEquals(1, model.getStatuses().size());
    assertFalse(model.getStatuses().containsKey("numLefts"));
    model.load("Test");
    assertEquals(1, model.getStatuses().size());
    assertEquals(5, (int) model.getStatuses().get("test"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void removeStatusNullName() {
    model.load("Go Right!");
    model.removeStatus(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void removeStatusNoSuch() {
    model.load("Go Right!");
    model.removeStatus("noSuchName");
  }

  @Test(expected = IllegalArgumentException.class)
  public void removeStatusConsequence() {
    model.load("Go Right!");
    model.removeStatus("numLefts");
  }

  @Test(expected = IllegalArgumentException.class)
  public void removeStatusSimpleDependentDependency() {
    try {
      model.load("Go Right!");
      model.addChoice();
      model.addStatus("total", 0);
      model.addSimpleDependentThresholdDecision("decide", 1, "total", 0, 0, 2);
    } catch (IllegalArgumentException e) {
      // To prove it doesn't happen here
    }
    model.removeStatus("total");
  }

  @Test(expected = IllegalArgumentException.class)
  public void removeStatusConsequentialDependentDependency() {
    try {
      model.load("Go Right!");
      model.addChoice();
      model.addStatus("total", 0);
      model.addConsequentialThresholdDecision("decide", 1, "total", 0, 0, 2,
          Collections.singletonList("ADD 1 numLefts"));
    } catch (IllegalArgumentException e) {
      // To prove it doesn't happen here
    }
    model.removeStatus("total");
  }

  @Test(expected = IllegalArgumentException.class)
  public void removeStatusConsequentialDependentConsequence() {
    try {
      model.load("Go Right!");
      model.addChoice();
      model.addStatus("total", 0);
      model.addConsequentialThresholdDecision("decide", 1, "numLefts", 0, 0, 2,
          Arrays.asList("ADD 2 numLefts", "SET 10 total"));
    } catch (IllegalArgumentException e) {
      // To prove it doesn't happen here
    }
    model.removeStatus("total");
  }

  @Test(expected = IllegalStateException.class)
  public void removeStatusNoneLoaded() {
    model.removeStatus("status");
  }

  @Test
  public void addChoice() {
    model.start("Test");
    model.load("Go Right!");
    model.addChoice();
    assertEquals(3, model.getChoices().size());
    model.load("Test");
    assertEquals(0, model.getChoices().size());
  }

  @Test
  public void setFirstChoice() {
    model.start("Test");
    model.load("Go Right!");
    model.setInitialChoice(1);
    assertEquals(1, model.getInitialChoice());
    StoryGame story = model.create();
    assertEquals("Game over, no choices left.", story.getCurrentChoice().toString());
    model.load("Test");
    assertEquals(-1, model.getInitialChoice());
  }

  @Test(expected = IllegalArgumentException.class)
  public void setFirstChoiceInvalidIdxNegative() {
    model.load("Go Right!");
    model.setInitialChoice(-1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void setFirstChoiceInvalidIdxAbove() {
    model.load("Go Right!");
    model.setInitialChoice(2);
  }

  @Test(expected = IllegalStateException.class)
  public void setFirstChoiceNoneLoaded() {
    model.setInitialChoice(0);
  }

  @Test
  public void addSimpleDecision() {
    model.start("Test");
    model.load("Go Right!");
    model.addChoice();
    model.addSimpleDecision("End", 1, 2);
    assertEquals(4, model.getDecisions().size());
    assertTrue(Arrays.toString(model.getDecisions().toArray()).contains("End"));
    StoryGame story = model.create();
    story.next(0);
    assertEquals("End", story.getCurrentChoice().toString());
    story.next(0);
    assertEquals("Game over, no choices left.", story.getCurrentChoice().toString());
    model.load("Test");
    assertEquals(0, model.getDecisions().size());
  }

  @Test(expected = IllegalArgumentException.class)
  public void addSimpleDecisionNull() {
    model.load("Go Right!");
    model.addSimpleDecision(null, 0, 1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void addSimpleDecisionInvalidIdxNegativeChoice() {
    model.load("Go Right!");
    model.addSimpleDecision("", -1, 1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void addSimpleDecisionInvalidIdxAboveChoice() {
    model.load("Go Right!");
    model.addSimpleDecision("", 2, 1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void addSimpleDecisionInvalidIdxNegativeOutcome() {
    model.load("Go Right!");
    model.addSimpleDecision("", 0, -1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void addSimpleDecisionInvalidIdxAboveOutcome() {
    model.load("Go Right!");
    model.addSimpleDecision("", 0, 2);
  }

  @Test(expected = IllegalStateException.class)
  public void addSimpleDecisionNoneLoaded() {
    model.addSimpleDecision("", 0, 1);
  }

  @Test
  public void addConsequentialDecision() {
    model.start("Test");
    model.load("Go Right!");
    model.addChoice();
    model.addConsequentialDecision("Get lost", 1, 2,
        Arrays.asList("ADD 20 numLefts", "SET 30 numStraights"));
    assertEquals(4, model.getDecisions().size());
    assertTrue(Arrays.toString(model.getDecisions().toArray()).contains("Get lost"));
    StoryGame story = model.create();
    story.next(1);
    story.next(2);
    story.next(0);
    assertEquals("Get lost", story.getCurrentChoice().toString());
    story.next(0);
    assertEquals("Game over, no choices left.", story.getCurrentChoice().toString());
    assertEquals(21, (int) story.getStatuses().get("numLefts"));
    assertEquals(30, (int) story.getStatuses().get("numStraights"));
    model.load("Test");
    assertEquals(0, model.getDecisions().size());
  }

  @Test(expected = IllegalArgumentException.class)
  public void addConsequentialDecisionNullDescription() {
    model.load("Go Right!");
    model.addConsequentialDecision(null, 0, 1, new ArrayList<>());
  }

  @Test(expected = IllegalArgumentException.class)
  public void addConsequentialDecisionNullList() {
    model.load("Go Right!");
    model.addConsequentialDecision("", 0, 1, null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void addConsequentialDecisionInvalidIdxNegativeChoice() {
    model.load("Go Right!");
    model.addConsequentialDecision("", -1, 1, new ArrayList<>());
  }

  @Test(expected = IllegalArgumentException.class)
  public void addConsequentialDecisionInvalidIdxAboveChoice() {
    model.load("Go Right!");
    model.addConsequentialDecision("", 2, 1, new ArrayList<>());
  }

  @Test(expected = IllegalArgumentException.class)
  public void addConsequentialDecisionInvalidIdxNegativeOutcome() {
    model.load("Go Right!");
    model.addConsequentialDecision("", 0, -1, new ArrayList<>());
  }

  @Test(expected = IllegalArgumentException.class)
  public void addConsequentialDecisionInvalidIdxAboveOutcome() {
    model.load("Go Right!");
    model.addConsequentialDecision("", 0, 2, new ArrayList<>());
  }

  @Test(expected = IllegalArgumentException.class)
  public void addConsequentialDecisionStatusNotPresent() {
    model.load("Go Right!");
    model.addChoice();
    model.addConsequentialDecision("Get lost", 1, 2,
        Arrays.asList("ADD 20 numLefts", "SET 20 none"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void addConsequentialDecisionStatusInvalidFormat() {
    model.load("Go Right!");
    model.addChoice();
    model.addConsequentialDecision("Get lost", 1, 2,
        Arrays.asList("ADD 20", "SET 20 none"));
  }

  @Test(expected = IllegalStateException.class)
  public void addConsequentialDecisionNoneLoaded() {
    model.addConsequentialDecision("", 0, 1, new ArrayList<>());
  }

  @Test
  public void addSimpleDependentThresholdDecision() {
    model.load("Go Right!");
    int threshold = 25;
    model.addChoice();
    model.addSimpleDependentThresholdDecision("End", 1, "numLefts", threshold, 0, 2);
    assertEquals(4, model.getDecisions().size());
    assertTrue(Arrays.toString(model.getDecisions().toArray()).contains("End"));
    StoryGame story = model.create();
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

  @Test(expected = IllegalStateException.class)
  public void addSimpleDependentThresholdDecisionNoneLoaded() {
    model.addChoice();
    model.addSimpleDependentThresholdDecision("End", 1, "numLefts", 2, 0, 2);
  }


  @Test
  public void addConsequentialThresholdDecision() {
    model.load("Go Right!");
    int threshold = 25;
    model.addStatus("number of ends", 0);
    model.addChoice();
    model.addConsequentialThresholdDecision("End", 1, "numLefts", threshold, 0, 2,
        Collections.singletonList("ADD 1 \"number of ends\""));
    assertEquals(4, model.getDecisions().size());
    assertTrue(Arrays.toString(model.getDecisions().toArray()).contains("End"));
    StoryGame story = model.create();
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

  @Test(expected = IllegalStateException.class)
  public void addConsequentialThresholdDecisionNoneLoaded() {
    model.addConsequentialThresholdDecision("", 0, "numLefts", 2, 0, 1, new ArrayList<>());
  }

  @Test
  public void removeDecision() {
    model.start("Test");
    model.load("Test");
    model.addChoice();
    model.addSimpleDecision("test", 0, 0);
    model.load("Go Right!");
    model.removeDecision(0, 1);
    assertEquals(2, model.getDecisions().size());
    assertEquals("[Go right, Go straight]", model.getDecisions().toString());
    StoryGame story = model.create();
    assertEquals("Go right(1) or Go straight(2)", story.getCurrentChoice().toString());
    model.load("Test");
    assertEquals(1, model.getDecisions().size());
  }

  @Test(expected = IllegalArgumentException.class)
  public void removeDecisionInvalidIdxNegativeChoice() {
    model.load("Go Right!");
    model.removeDecision(-1, 0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void removeDecisionInvalidIdxAboveChoice() {
    model.load("Go Right!");
    model.removeDecision(2, 0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void removeDecisionInvalidIdxNegativeDecision() {
    model.load("Go Right!");
    model.removeDecision(0, -1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void removeDecisionInvalidIdxAboveDecision() {
    model.load("Go Right!");
    model.removeDecision(0, 3);
  }

  @Test(expected = IllegalStateException.class)
  public void removeDecisionNoneLoaded() {
    model.removeDecision(0, 0);
  }

  @Test
  public void removeChoice() {
    model.load("Go Right!");
    model.addChoice();
    model.removeChoice(2);
    assertEquals(2, model.getChoices().size());
  }

  @Test(expected = IllegalArgumentException.class)
  public void removeChoiceInvalidIdxNegative() {
    model.load("Go Right!");
    model.removeChoice(-1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void removeChoiceInvalidIdxAbove() {
    model.load("Go Right!");
    model.removeChoice(2);
  }

  @Test(expected = IllegalArgumentException.class)
  public void removeChoiceInitial() {
    model.load("Go Right!");
    model.removeChoice(0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void removeChoiceConsequentialDependent() {
    model.load("Go Right!");
    model.setInitialChoice(1);
    model.removeChoice(0);
  }

  @Test(expected = IllegalStateException.class)
  public void removeChoiceNoneLoaded() {
    model.removeChoice(0);
  }

  @Test
  public void getDecisions() {
    assertNull(model.getDecisions());
    model.load("Go Right!");
    assertEquals("[Go right, Go left, Go straight]",
        Arrays.toString(model.getDecisions().toArray()));
  }
}

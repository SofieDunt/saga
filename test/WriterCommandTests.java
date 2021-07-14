import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import controller.command.AddChoice;
import controller.command.AddConsequentialDecision;
import controller.command.AddConsequentialDependentDecision;
import controller.command.AddSimpleDecision;
import controller.command.AddSimpleDependentDecision;
import controller.command.AddStatusToWork;
import controller.command.ExportWork;
import controller.command.LoadWork;
import controller.command.QuitWork;
import controller.command.RemoveChoice;
import controller.command.RemoveDecision;
import controller.command.RemoveWork;
import controller.command.RenameWork;
import controller.command.SetInitialChoice;
import controller.command.SetStoryName;
import controller.command.StartWork;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;
import model.SimpleStoryWriterModel;
import model.StoryWriterModel;
import model.game.StoryGame;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for {@link controller.command.Command}s and {@link controller.command.IOCommand}s that
 * command {@link StoryWriterModel}s.
 */
public class WriterCommandTests {

  private StoryWriterModel<StoryGame> model;

  @Before
  public void createGoRight() {
    model = new SimpleStoryWriterModel();
    new StartWork("Go Right!").execute(model);
    new LoadWork("Go Right!").execute(model);
    new AddStatusToWork("numLefts", "0").execute(model);
    new AddStatusToWork("numStraights", "0").execute(model);
    new AddChoice().execute(model); // right/left/straight
    new AddChoice().execute(model); // end
    new SetInitialChoice("1").execute(model);
    new AddSimpleDecision("Go right", "1", "2").execute(model);
    new AddConsequentialDecision("Go left", "1", "1", "ADD 1 numLefts").execute(model);
    new AddConsequentialDecision("Go straight", "1", "1", "ADD 1 numStraights").execute(model);
    new QuitWork().execute(model);
  }

  @Test
  public void testCreateGoRight() {
    assertNull(model.getCurrentWorkName());
    assertNull(model.getChoices());
    assertNull(model.getDecisions());
    assertEquals(-1234567890, model.getInitialChoice());
    assertNull(model.getStatuses());
    assertNull(model.getStoryName());
    new LoadWork("Go Right!").execute(model);
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
  public void start() {
    new StartWork("Go Right!").execute(model);
    assertEquals(Arrays.asList("Go Right!", "Go Right!(1)"), model.getAllWorkNames());

    new LoadWork("Go Right!(1)").execute(model);
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
    new StartWork(null).execute(model);
    assertEquals(Arrays.asList("Go Right!", "Untitled"), model.getAllWorkNames());
    new StartWork(null).execute(model);
    assertEquals(Arrays.asList("Go Right!", "Untitled", "Untitled(1)"), model.getAllWorkNames());
  }

  @Test
  public void remove() {
    new RemoveWork("Go Right!").execute(model);
    assertEquals(0, model.getAllWorkNames().size());

    // Remove other while working on current
    new StartWork("a").execute(model);
    new StartWork("b").execute(model);
    new LoadWork("a").execute(model);
    new RemoveWork("b").execute(model);
    assertEquals("a", model.getCurrentWorkName());
    assertEquals(1, model.getAllWorkNames().size());
  }

  @Test(expected = IllegalArgumentException.class)
  public void removeNoSuch() {
    new RemoveWork("No such").execute(model);
  }

  @Test
  public void rename() {
    new RenameWork("Go Right!", "right draft").execute(model);
    assertEquals(Collections.singletonList("right draft"), model.getAllWorkNames());
    new RenameWork("right draft", "Right draft").execute(model);
    assertEquals(Collections.singletonList("Right draft"), model.getAllWorkNames());
  }

  @Test(expected = IllegalArgumentException.class)
  public void renameInvalidNewNull() {
    new RenameWork("Go Right!", null).execute(model);
  }

  @Test(expected = IllegalArgumentException.class)
  public void loadNull() {
    new LoadWork(null).execute(model);
  }

  @Test
  public void export() throws IOException {
    new LoadWork("Go Right!").execute(model);
    String referencePath = "./res/right.txt";
    new ExportWork(null).execute(model);
    File exported = new File("./Go Right!.txt");
    Scanner sc1 = new Scanner(new FileInputStream(referencePath));
    Scanner sc2 = new Scanner(new FileInputStream(exported));

    while (sc1.hasNext()) {
      assertTrue(sc2.hasNext());
      assertEquals(sc1.next(), sc2.next());
    }

    assertTrue(exported.delete());

    new ExportWork("./res/test.txt").execute(model);
    exported = new File("./res/test.txt");
    sc1.reset();
    sc2 = new Scanner(new FileInputStream(exported));
    while (sc1.hasNext()) {
      assertTrue(sc2.hasNext());
      assertEquals(sc1.next(), sc2.next());
    }

    assertTrue(exported.delete());
  }

  @Test(expected = IllegalStateException.class)
  public void exportNoneLoaded() throws IOException {
    new ExportWork(null).execute(model);
  }

  @Test
  public void setStoryName() {
    new LoadWork("Go Right!").execute(model);
    new SetStoryName("right").execute(model);
  }

  @Test(expected = IllegalArgumentException.class)
  public void setStoryNameNull() {
    new LoadWork("Go Right!").execute(model);
    new SetStoryName(null).execute(model);
  }


  @Test(expected = IllegalStateException.class)
  public void setStoryNameNoneLoaded() {
    new SetStoryName("None").execute(model);
  }


  @Test
  public void addStatusToWork() {
    model.start("Test");
    model.load("Go Right!");
    new AddStatusToWork("total", "0").execute(model);
    assertEquals(3, model.getStatuses().size());
    assertEquals(0, (int) model.getStatuses().get("total"));
    StoryGame story = model.create();
    assertEquals(3, story.getStatuses().size());
    assertEquals(0, (int) story.getStatuses().get("total"));
    // Update
    new AddStatusToWork("total", "200").execute(model);
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
    new AddStatusToWork(null, "0").execute(model);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAddStatusConstructorVal() {
    new AddStatusToWork("null", "not");
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

  @Test(expected = IllegalArgumentException.class)
  public void testSetInitialChoiceConstructor() {
    new SetInitialChoice("-1").execute(model);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAddSimpleDecisionConstructorChoiceIdx() {
    new AddSimpleDecision("", "0", "1");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAddSimpleDecisionConstructorOutcomeIdx() {
    new AddSimpleDecision("", "1", "0");
  }

  @Test(expected = IllegalArgumentException.class)
  public void addConsequentialDecisionConstructorNullConsequences() {
    model.load("Go Right!");
    new AddConsequentialDecision("", "1", "1", null).execute(model);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAddConsequentialDecisionConstructorChoiceIdx() {
    new AddConsequentialDecision("", "0", "1", "");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAddConsequentialDecisionConstructorOutcomeIdx() {
    new AddConsequentialDecision("", "1", "-1", "");
  }

  @Test
  public void addSimpleDependentThresholdDecision() {
    model.load("Go Right!");
    int threshold = 25;
    model.addChoice();
    new AddSimpleDependentDecision("End", "2", "numLefts", "25", "1", "3").execute(model);
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


  @Test(expected = IllegalArgumentException.class)
  public void testAddSimpleDependentDecisionConstructorChoiceIdx() {
    new AddSimpleDependentDecision("End", "0", "numLefts", "25", "1", "3");

  }

  @Test(expected = IllegalArgumentException.class)
  public void testAddSimpleDependentDecisionConstructorOutcomeBelowIdx() {
    new AddSimpleDependentDecision("End", "2", "numLefts", "25", "0", "3");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAddSimpleDependentDecisionConstructorOutcomeMeetsIdx() {
    new AddSimpleDependentDecision("End", "2", "numLefts", "25", "1", "0");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAddSimpleDependentDecisionConstructorThreshold() {
    new AddSimpleDependentDecision("End", "2", "numLefts", "not", "1", "0");
  }

  @Test
  public void addConsequentialThresholdDecision() {
    model.load("Go Right!");
    int threshold = 25;
    model.addStatus("number of ends", 0);
    model.addChoice();
    new AddConsequentialDependentDecision("End", "2", "numLefts", "25", "1", "3",
        "ADD 1 \"number of ends\"").execute(model);
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

  @Test(expected = IllegalArgumentException.class)
  public void testAddConsequentialDependentDecisionConstructorChoiceIdx() {
    new AddConsequentialDependentDecision("End", "0", "numLefts", "25", "1", "3",
        "ADD 1 \"number of ends\"");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAddConsequentialDependentDecisionConstructorOutcomeBelowIdx() {
    new AddConsequentialDependentDecision("End", "1", "numLefts", "25", "0", "3",
        "ADD 1 \"number of ends\"");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAddConsequentialDependentDecisionConstructorOutcomeMeetsIdx() {
    new AddConsequentialDependentDecision("End", "1", "numLefts", "25", "1", "0",
        "ADD 1 \"number of ends\"");
  }


  @Test(expected = IllegalArgumentException.class)
  public void testAddConsequentialDependentDecisionConstructorThreshold() {
    new AddConsequentialDependentDecision("End", "1", "numLefts", "not", "1", "1",
        "ADD 1 \"number of ends\"");
  }

  @Test(expected = IllegalArgumentException.class)
  public void addConsequentialDependentDecisionNullConsequences() {
    model.load("Go Right!");
    new AddConsequentialDependentDecision("End", "1", "numLefts", "25", "1", "1", null)
        .execute(model);
  }

  @Test
  public void removeDecision() {
    model.start("Test");
    model.load("Test");
    model.addChoice();
    model.addSimpleDecision("test", 0, 0);
    model.load("Go Right!");
    new RemoveDecision("1", "2").execute(model);
    assertEquals(2, model.getDecisions().size());
    assertEquals("[Go right, Go straight]", model.getDecisions().toString());
    StoryGame story = model.create();
    assertEquals("Go right(1) or Go straight(2)", story.getCurrentChoice().toString());
    model.load("Test");
    assertEquals(1, model.getDecisions().size());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testRemoveDecisionConstructorChoiceIdx() {
    new RemoveDecision("-1", "1");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testRemoveDecisionConstructorDecisionIdx() {
    new RemoveDecision("1", "0");
  }

  @Test
  public void removeChoice() {
    model.load("Go Right!");
    model.addChoice();
    new RemoveChoice("3").execute(model);
    assertEquals(2, model.getChoices().size());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testRemoveChoiceConstructor() {
    new RemoveChoice("0");
  }
}

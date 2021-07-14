import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import controller.ApplicationController;
import controller.WriterController;
import java.io.File;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Collections;
import model.SimpleStoryWriterModel;
import model.StoryWriterModel;
import model.game.StoryGame;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for {@link WriterController}s.
 */
public class WriterControllerTest extends BadIOControllerTests {

  private final StoryWriterModel<StoryGame> model = new SimpleStoryWriterModel();
  private final Appendable output = new StringBuilder();
  private final Appendable rightOutput = new StringBuilder();

  @Override
  protected ApplicationController controller(Appendable appendable) {
    return new WriterController(new SimpleStoryWriterModel(), new StringReader(""), appendable);
  }

  @Override
  protected ApplicationController controller(Readable input) {
    return new WriterController(model, input, output);
  }

  /**
   * Checks whether the given string was appended to the destination.
   *
   * @param str the string
   * @return true if it was appended, false otherwise.
   */
  private boolean didOutput(String str) {
    return output.toString().contains(str);
  }

  @Before
  public void createGoRight() {
    new WriterController(model,
        new StringReader(
            "start \"Go Right!\" "
                + "load \"Go Right!\" "
                + "addStatus numLefts 0 "
                + "addStatus numStraights 0 "
                + "addChoice "
                + "addChoice "
                + "setInitial 1 "
                + "addSimple \"Go right\" 1 2 "
                + "addConsequential \"Go left\" 1 1 \"ADD 1 numLefts\" "
                + "addConsequential \"Go straight\" 1 1 \"ADD 1 numStraights\" "
                + "quit"
        ),
        rightOutput).play();
  }

  @Test
  public void testCreateGoRight() {
    // Model
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
    // View
    assertTrue(rightOutput.toString().contains("Name: Go Right!\n"
        + "Statuses:\n"
        + "Name: numLefts, Initial Value: 0\n"
        + "Name: numStraights, Initial Value: 0\n"
        + "Choice #1: Go right(1), Go left(2), or Go straight(3) "
        + "[ Decision #1 Decision #2 Decision #3 ]\n"
        + "Choice #2: Game over, no choices left. [ ]\n"
        + "Decision #1: Go right ( SIMPLE \"Go right\" Choice #2 )\n"
        + "Decision #2: Go left ( CONSEQUENTIAL \"Go left\" [ ADD 1 \"numLefts\" ] Choice #1 )\n"
        + "Decision #3: Go straight "
        + "( CONSEQUENTIAL \"Go straight\" [ ADD 1 \"numStraights\" ] Choice #1 )\n"));
    assertFalse(rightOutput.toString().contains("Bad input"));
  }

  @Test
  public void testConstructor() {
    new WriterController(new SimpleStoryWriterModel(), new StringReader(""), output).play();
    assertEquals("Welcome to the story creator!\n"
        + "\n"
        + "You don't have any works in your library. Start one now!\n"
        + "Enter a valid command:\n"
        + "Application closed.", output.toString());
  }

  @Test
  public void export() {
    String exportPath = "./res/writerControllerExport.txt";
    controller(new StringReader("load \"Go Right!\" export " + exportPath)).play();
    // Check controlled view
    assertTrue(output.toString().contains("Enter path to export to:"));
    assertTrue(output.toString().contains("SUCCESS: export\n"));
    assertTrue(new File(exportPath).delete());
  }

  @Test
  public void exportNull() {
    String defaultPath = "./Go Right!.txt";
    controller(new StringReader("load \"Go Right!\" export ")).play();
    // Check controlled view
    assertTrue(output.toString().contains("Enter path to export to:"));
    assertTrue(output.toString().contains("SUCCESS: export\n"));
    assertTrue(new File(defaultPath).delete());
  }

  @Test
  public void start() {
    controller(new StringReader("start \"Go Right!\"")).play();
    assertEquals(Arrays.asList("Go Right!", "Go Right!(1)"), model.getAllWorkNames());
    // When none loaded
    assertNull(model.getCurrentWorkName());
    assertNull(model.getChoices());
    assertNull(model.getDecisions());
    assertEquals(-1234567890, model.getInitialChoice());
    assertNull(model.getStatuses());
    assertNull(model.getStoryName());

    controller(new StringReader("load \"Go Right!(1)\"")).play();
    assertEquals("Go Right!(1)", model.getCurrentWorkName());
    // Newly created story
    assertEquals(0, model.getChoices().size());
    assertEquals(0, model.getDecisions().size());
    assertEquals(-1, model.getInitialChoice());
    assertEquals(0, model.getStatuses().size());
    assertEquals("Go Right!", model.getStoryName()); // story kept original name
    assertTrue(didOutput("Your library:\n"
        + "Go Right!\n"
        + "Go Right!(1)\n"
        + "\n"
        + "Enter a valid command:\n"
        + "Enter work name:\n"
        + "\n"
        + "Work Name: Go Right!(1)\n"
        + "Story Name: Go Right!\n"
        + "Statuses:\n"));
  }

  @Test
  public void startNull() {
    controller(new StringReader("start")).play();
    assertEquals(Arrays.asList("Go Right!", "Untitled"), model.getAllWorkNames());
    controller(new StringReader("start")).play();
    assertEquals(Arrays.asList("Go Right!", "Untitled", "Untitled(1)"), model.getAllWorkNames());
    assertTrue(didOutput("Enter a valid command:\n"
        + "Enter new work name:\n"
        + "\n"
        + "Your library:\n"
        + "Go Right!\n"
        + "Untitled\n"
        + "Untitled(1)\n"
        + "\n"
        + "Enter a valid command:\n"
        + "Application closed."));
  }

  @Test
  public void remove() {
    controller(new StringReader("remove \"Go Right!\"")).play();
    assertEquals(0, model.getAllWorkNames().size());
    assertTrue(didOutput("You don't have any works in your library. Start one now!"));
  }

  @Test
  public void removeCurrent() {
    controller(new StringReader("load \"Go Right!\" remove \"Go Right!\"")).play();
    assertEquals(0, model.getAllWorkNames().size());
    assertNull(model.getCurrentWorkName());
    assertNull(model.getChoices());
    assertNull(model.getDecisions());
    assertEquals(-1234567890, model.getInitialChoice());
    assertNull(model.getStatuses());
    assertNull(model.getStoryName());
    assertTrue(didOutput("You don't have any works in your library. Start one now!"));
  }

  @Test
  public void removeNoSuch() {
    controller(new StringReader("remove \"No such\"")).play();
    assertTrue(didOutput("Could not remove: No work \"No such\" found"));
  }

  @Test
  public void rename() {
    controller(new StringReader("rename \"Go Right!\" \"right draft\"")).play();
    assertTrue(didOutput("Your library:\nright draft"));
  }

  @Test
  public void renameCurrent() {
    controller(new StringReader("load \"Go Right!\" rename \"Go Right!\" Right")).play();
    assertEquals(Collections.singletonList("Right"), model.getAllWorkNames());
    assertEquals("Right", model.getCurrentWorkName());
    assertEquals("Go Right!", model.getStoryName());
    assertTrue(didOutput("Work Name: Go Right!\nStory Name: Go Right!"));
    assertTrue(didOutput("Work Name: Right\nStory Name: Go Right!"));
  }

  @Test
  public void renameNoSuch() {
    controller(new StringReader("rename \"No such\" name")).play();
    assertTrue(didOutput("Could not rename: No work \"No such\" found"));
  }

  @Test
  public void renameInvalidNewNull() {
    controller(new StringReader("rename \"Go Right!\"")).play();
    assertTrue(didOutput("Could not rename: Name can't be null"));
  }

  @Test
  public void renameInvalidNewItself() {
    controller(new StringReader("rename \"Go Right!\" \"Go Right!\"")).play();
    assertTrue(didOutput("Could not rename: Go Right! already exists"));
  }

  @Test
  public void loadNull() {
    controller(new StringReader("load")).play();
    assertTrue(didOutput("Could not load: No work \"null\" found"));
  }

  @Test
  public void loadNoSuch() {
    controller(new StringReader("load \"No such\"")).play();
    assertTrue(didOutput("Could not load: No work \"No such\" found"));
  }

  @Test
  public void setStoryName() {
    controller(new StringReader("load \"Go Right!\" setName right")).play();
    assertEquals("right", model.getStoryName());
    StoryGame story = model.create();
    assertEquals("right", story.getName());
    assertTrue(didOutput("Work Name: Go Right!\nStory Name: right\n"));
  }

  @Test
  public void setStoryNameNull() {
    controller(new StringReader("load \"Go Right!\" setName")).play();
    assertTrue(didOutput("Could not setName: Name can't be null"));
  }


  @Test
  public void setStoryNameNoneLoaded() {
    controller(new StringReader("setName None")).play();
    assertTrue(didOutput("Could not setName: No loaded story!"));
  }

  @Test
  public void addStatusNullName() {
    controller(new StringReader("addStatus")).play();
    assertTrue(didOutput("Could not addStatus: Value must be an integer"));
  }

  @Test
  public void addStatusNoneLoaded() {
    controller(new StringReader("addStatus stat 0")).play();
    assertTrue(didOutput("Could not addStatus: No loaded story!"));
  }

  @Test
  public void removeStatusNullName() {
    controller(new StringReader("load \"Go Right!\" removeStatus")).play();
    assertTrue(didOutput("Could not removeStatus: Status name can't be null"));
  }

  @Test
  public void removeStatusNoSuch() {
    controller(new StringReader("load \"Go Right!\" removeStatus noSuchName")).play();
    System.out.println(output);
    assertTrue(didOutput("Could not removeStatus: No status noSuchName in the story"));
  }

  @Test
  public void removeStatusConsequence() {
    controller(new StringReader("load \"Go Right!\" removeStatus numLefts")).play();
    assertTrue(didOutput("Could not removeStatus: At least one decision references this status"));
  }

  @Test
  public void removeStatusSimpleDependentDependency() {
    controller(new StringReader(
        "load \"Go Right!\" addChoice addStatus total 0 "
            + "addSimpleDependent decide 2 total 0 1 3 removeStatus total"))
        .play();
    assertTrue(didOutput("Could not removeStatus: At least one decision references this status"));
    assertFalse(didOutput("Could not addChoice"));
    assertFalse(didOutput("Could not addStatus"));
    assertFalse(didOutput("Could not addSimpleDependent"));
  }

  @Test
  public void removeStatusNoneLoaded() {
    controller(new StringReader("removeStatus statusName")).play();
    assertTrue(didOutput("Could not removeStatus: No loaded story!"));
  }

  @Test
  public void setFirstChoiceInvalidIdxZero() {
    controller(new StringReader("load \"Go Right!\" setInitial 0 1")).play();
    assertTrue(didOutput("Bad input. Enter choice number:"));
    assertFalse(didOutput("Could not setInitial"));
  }

  @Test
  public void addSimpleDecisionNull() {
    controller(new StringReader("load \"Go Right!\" addSimple")).play();
    assertTrue(didOutput("Could not addSimple: Indices must be positive integers"));
  }

  @Test
  public void addSimpleDecisionBadInput() {
    controller(new StringReader("load \"Go Right!\" addSimple decision 0 -1 1 0 bad 2")).play();
    assertTrue(didOutput("Enter a valid command:\n"
        + "Enter description:\n"
        + "Enter choice number:\n"
        + "Bad input. Enter choice number:\n"
        + "Bad input. Enter choice number:\n"
        + "Enter outcome number:\n"
        + "Bad input. Enter outcome number:\n"
        + "Bad input. Enter outcome number:"));
  }

  @Test
  public void addConsequentialDecisionNull() {
    controller(new StringReader("load \"Go Right!\" addConsequential")).play();
    assertTrue(didOutput("Enter a valid command:\n"
        + "Enter description:\n"
        + "Enter choice number:\n"
        + "Enter outcome number:\n"
        + "Enter status update consequences, separated by commas:\n"
        + "Could not addConsequential: Indices must be positive integers"));
  }

  @Test
  public void addConsequentialDecisionBadInput() {
    controller(new StringReader(
        "load \"Go Right!\" addConsequential name -1 bad 0 1 bad 2 badConsequence")).play();
    assertTrue(didOutput("Enter a valid command:\n"
        + "Enter description:\n"
        + "Enter choice number:\n"
        + "Bad input. Enter choice number:\n"
        + "Bad input. Enter choice number:\n"
        + "Bad input. Enter choice number:\n"
        + "Enter outcome number:\n"
        + "Bad input. Enter outcome number:\n"
        + "Enter status update consequences, separated by commas:\n"
        + "Could not addConsequential: Illegal format - not a valid status update"));
  }

  @Test
  public void addConsequentialDependentDecisionBadInput() {
    controller(new StringReader("load \"Go Right!\""
        + " addConsequentialDependent name -1 bad 0 1 numLefts bad 2 0 1 0 2 \"SET 4 numLefts\""))
        .play();
    System.out.println(output);
    assertTrue(didOutput("Enter a valid command:\n"
        + "Enter description:\n"
        + "Enter choice number:\n"
        + "Bad input. Enter choice number:\n"
        + "Bad input. Enter choice number:\n"
        + "Bad input. Enter choice number:\n"
        + "Enter status dependency name:\n"
        + "Enter threshold value:\n"
        + "Bad input. Enter threshold value:\n"
        + "Enter the number of the outcome if the value is below the threshold:\n"
        + "Bad input. Enter the number of the outcome if the value is below the threshold:\n"
        + "Enter the number of the outcome if the value meets the threshold:\n"
        + "Bad input. Enter the number of the outcome if the value meets the threshold:\n"
        + "Enter status update consequences, separated by commas:"));
    assertTrue(didOutput(
        "Decision #4: name ( DEPENDENT TWOTHRESHOLD \"numLefts\" 2 "
            + "Choice #1 Choice #2 "
            + "[ CONSEQUENTIAL \"name\" [ SET 4 \"numLefts\" ] Choice #1 ] )\n"));
  }

  @Test
  public void removeDecisionBadInput() {
    controller(new StringReader("load \"Go Right!\" removeDecision -1 0 bad 1 bad 0 1")).play();
    assertTrue(didOutput("Enter a valid command:\n"
        + "Enter choice number:\n"
        + "Bad input. Enter choice number:\n"
        + "Bad input. Enter choice number:\n"
        + "Bad input. Enter choice number:\n"
        + "Enter option number:\n"
        + "Bad input. Enter option number:\n"
        + "Bad input. Enter option number:"));
    assertTrue(didOutput("Choice #1: Go left(1) or Go straight(2)"));
  }

  @Test
  public void removeChoiceBadInputAndRequest() {
    controller(new StringReader("load \"Go Right!\" removeChoice -1 0 1")).play();
    assertTrue(didOutput("Enter a valid command:\n"
        + "Enter choice number:\n"
        + "Bad input. Enter choice number:\n"
        + "Bad input. Enter choice number:\n"
        + "Could not removeChoice: Choice is an outcome of an option or is the first choice"));
  }
}

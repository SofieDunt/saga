import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import controller.ApplicationController;
import controller.PlayerController;
import controller.command.PlayStory;
import controller.command.Restart;
import java.io.StringReader;
import model.SimpleStoryPlayerModel;
import model.StoryPlayerModel;
import model.game.StoryGame;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests to ensure {@link PlayerController}s control their models and view correctly.
 */
public class PlayerControllerTest extends BadIOControllerTests {

  private final StoryPlayerModel<StoryGame> model = new SimpleStoryPlayerModel();
  private final Appendable output = new StringBuilder();


  @Override
  protected ApplicationController controller(Appendable appendable) {
    return new PlayerController(new SimpleStoryPlayerModel(), new StringReader(""), appendable);
  }

  @Override
  protected ApplicationController controller(Readable input) {
    return new PlayerController(model, input, output);
  }

  @Before
  public void initData() {
    model.addStory(TestDataProvider.goRight());
  }

  @Test
  public void testQuit() {
    controller(new StringReader("q ignores everything after")).play();
    assertEquals("Welcome to the story player!\n"
        + "Your story library:\n"
        + "Go Right!\n"
        + "\n"
        + "Enter a valid command:\n"
        + "Application closed.", output.toString());
    int from = output.toString().length();
    controller(new StringReader("export path q q ignores everything after")).play();
    assertEquals("Welcome to the story player!\n"
        + "Your story library:\n"
        + "Go Right!\n"
        + "\n"
        + "Enter a valid command:\n"
        + "Enter path to save story to:\n"
        + "Enter name of story to export:\n"
        + "Could not export: No story \"q\" found\n"
        + "Your story library:\n"
        + "Go Right!\n"
        + "\n"
        + "Enter a valid command:\n"
        + "Application closed.", output.toString().substring(from));
  }

  @Test
  public void testExportImportStory() {
    String exportPath = "./res/controllerTest/controllerExport.txt";
    model.playStory("Go Right!");
    model.next(1);
    controller(new StringReader(
        "exportInProgress " + exportPath + " \"Go Right!\" import " + exportPath
    )).play();

    // Check controlled model
    assertEquals(1, (int) model.getStory("Go Right!(1)").getStatuses().get("numLefts"));
    // Check controlled view
    assertTrue(output.toString().contains("Enter path to save story to:"));
    assertTrue(output.toString().contains("Enter name of story to export:"));
    assertTrue(output.toString().contains("SUCCESS: exportInProgress\n"));
    assertTrue(output.toString().contains("Enter file path:\nSUCCESS: import"));
    int from = output.toString().length();

    controller(new StringReader(
        "export " + exportPath + " \"Go Right!\" import " + exportPath
    )).play();

    // Check controlled model
    assertEquals(0, (int) model.getStory("Go Right!(2)").getStatuses().get("numLefts"));
    // Check controlled view
    assertTrue(output.toString().substring(from).contains("Enter path to save story to:"));
    assertTrue(output.toString().substring(from).contains("Enter name of story to export:"));
    assertTrue(output.toString().substring(from).contains("SUCCESS: export"));
    assertTrue(output.toString().substring(from).contains("Enter file path:\nSUCCESS: import"));
  }

  @Test
  public void testChoose() {
    model.playStory("Go Right!");
    controller(new StringReader("choose 1")).play();
    // Model
    assertEquals("Game over, no choices left.", model.getCurrentChoice());
    // View
    assertTrue(output.toString().contains("Enter a numeric decision:"));
    assertTrue(output.toString().contains("Game over, no choices left."));
    int from = output.toString().length();

    model.restart();
    controller(new StringReader("choose 2")).play();
    // Model
    assertEquals(1, (int) model.getStory("Go Right!").getStatuses().get("numLefts"));
    // View
    assertTrue(
        output.toString().substring(from).contains("Go right(1), Go left(2), or Go straight(3)"));
    assertFalse(output.toString().substring(from).contains("Game over, no choices left."));
  }

  @Test
  public void testNext() {
    model.playStory("Go Right!");
    controller(new StringReader("next")).play();
    // Model
    assertEquals("Game over, no choices left.", model.getCurrentChoice());
    // View
    assertEquals("Welcome to the story player!\n"
        + "Go right(1), Go left(2), or Go straight(3)\n"
        + "Enter a valid command:\n"
        + "Game over, no choices left.\n"
        + "Enter a valid command:\n"
        + "Application closed.", output.toString());
  }

  @Test
  public void testPlayStory() {
    controller(new StringReader("play \"Go Right!\"")).play();
    new PlayStory("Go Right!").execute(model);
    // Model
    assertEquals("Go Right!", model.getCurrentStoryName());
    // View
    assertTrue(output.toString().contains("Enter story name:"));
    assertTrue(output.toString().contains("Enter story name:"));
    int from = output.toString().length();

    model.addStory(TestDataProvider.strengthStory());
    controller(new StringReader("play \"Strength!\"")).play();
    // Model
    assertEquals("Strength!", model.getCurrentStoryName());
    // View
    assertTrue(output.toString().substring(from).contains("Enter story name:"));
    assertTrue(
        output.toString().substring(from).contains(
            "get 1 strength(1), get 2 strength(2), get 3 strength(3), or don't get strength(4)"));
  }

  @Test
  public void testQuitStory() {
    model.playStory("Go Right!");
    controller(new StringReader("quit")).play();
    assertNull(model.getCurrentStoryName());
    assertTrue(output.toString().contains("Your story library:\nGo Right!\n"));
  }

  @Test
  public void testRemoveStory() {
    model.playStory("Go Right!");
    controller(new StringReader("remove \"Go Right!\"")).play();
    assertNull(model.getCurrentStoryName());
    assertTrue(
        output.toString().contains("You don't have any stories in your library. Import some!\n"));
  }

  @Test
  public void testRestart() {
    model.playStory("Go Right!");
    model.next(0);
    controller(new StringReader("restart")).play();
    new Restart().execute(model);
    assertEquals("Go right(1), Go left(2), or Go straight(3)", model.getCurrentChoice());
    assertTrue(output.toString().contains("Go right(1), Go left(2), or Go straight(3)"));
  }

  @Test
  public void testExportStoryBadInput() {
    controller(new StringReader("export \"./res/doesn't Exist/story.txt\" \"Go Right!\"")).play();
    assertTrue(output.toString()
        .contains("Could not export: Can't write to path: ./res/doesn't Exist/story.txt"));
  }

  @Test
  public void testImportStoryBadInput() {
    controller(new StringReader("import \"./res/doesn't Exist/story.txt\"")).play();
    assertTrue(output.toString().contains("Could not import: File not found"));
  }

  @Test
  public void testChooseBadInput() {
    model.playStory("Go Right!");
    controller(new StringReader("choose 4")).play();
    assertTrue(output.toString().contains("Could not choose: No choice 4"));
  }

  @Test
  public void testChooseNoneLoaded() {
    controller(new StringReader("choose 4")).play();
    assertTrue(output.toString().contains("Could not choose: No loaded story!"));
  }

  @Test
  public void testNextNoneLoaded() {
    controller(new StringReader("next")).play();
    assertTrue(output.toString().contains("Could not next: No loaded story!"));
  }

  @Test
  public void testPlayStoryBadInput() {
    controller(new StringReader("play \"Go Right!\"")).play();
    new PlayStory("Go Right!").execute(model);
    // Model
    assertEquals("Go Right!", model.getCurrentStoryName());
    // View
    assertTrue(output.toString().contains("Enter story name:"));
    assertTrue(output.toString().contains("Enter story name:"));
    int from = output.toString().length();

    controller(new StringReader("play \"Strength!\"")).play();
    // Model
    assertEquals("Go Right!", model.getCurrentStoryName());
    // View
    assertTrue(output.toString().substring(from).contains("Enter story name:"));
    assertTrue(
        output.toString().substring(from).contains("Could not play: No story \"Strength!\" found"));
  }

  @Test
  public void testRemoveStoryBadInput() {
    controller(new StringReader("remove None")).play();
    assertTrue(output.toString().contains("Could not remove: No story \"None\" found"));
  }

  @Test
  public void testRestartNoneLoaded() {
    controller(new StringReader("restart")).play();
    assertTrue(output.toString().contains("Could not restart: No loaded story!"));
  }

  @Test
  public void testExportStoryNullStory() {
    controller(new StringReader("export .")).play();
    assertTrue(output.toString().contains("Could not export: No story \"null\" found"));
  }

  @Test
  public void testImportNullPath() {
    controller(new StringReader("import")).play();
    assertTrue(output.toString().contains("Could not import: Filename can't be null"));
  }

  @Test
  public void testNullChoose() {
    controller(new StringReader("play \"Go Right!\" choose")).play();
    assertTrue(
        output.toString().contains("Could not choose: Decision must be a non-negative integer"));
  }

  @Test
  public void testNullPlay() {
    controller(new StringReader("play")).play();
    assertTrue(output.toString().contains("Could not play: No story \"null\" found"));
  }

  @Test
  public void testNullRemove() {
    controller(new StringReader("remove")).play();
    assertTrue(output.toString().contains("Could not remove: No story \"null\" found"));
  }

  @Test
  public void testPromptGoodInput() {
    controller(new StringReader("choose bad")).play();
    assertTrue(output.toString().contains("Bad input. Enter a numeric decision:"));
  }

  @Test
  public void testControllerEmptyInput() {
    controller(new StringReader("")).play();
    assertEquals("Welcome to the story player!\n"
        + "Your story library:\n"
        + "Go Right!\n"
        + "\n"
        + "Enter a valid command:\n"
        + "Application closed.", output.toString());
  }
}

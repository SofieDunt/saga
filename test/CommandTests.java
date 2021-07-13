import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import controller.command.ExportStory;
import controller.command.ImportStory;
import controller.command.Next;
import controller.command.PlayStory;
import controller.command.QuitStory;
import controller.command.RemoveStory;
import controller.command.Restart;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import model.SimpleStoryPlayerModel;
import model.StoryPlayerModel;
import model.game.StoryGame;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for {@link controller.command.Command}s and {@link
 * controller.command.IOCommand}s.
 */
public class CommandTests {

  private final StoryPlayerModel<StoryGame> model = new SimpleStoryPlayerModel();

  @Before
  public void initData() {
    model.addStory(TestDataProvider.goRight());
    model.addStory(TestDataProvider.strengthStory());
  }

  @Test
  public void testExportImportStory() throws IOException {
    String exportPath = "./res/controllerTest/story.txt";
    model.playStory("Go Right!");
    model.next(1);
    new ExportStory(exportPath, "Go Right!", false).execute(model);
    new ImportStory(exportPath).execute(model);
    assertEquals(1, (int) model.getStory("Go Right!(1)").getStatuses().get("numLefts"));
    new ExportStory(exportPath, "Go Right!", true).execute(model);
    new ImportStory(exportPath).execute(model);
    assertEquals(0, (int) model.getStory("Go Right!(2)").getStatuses().get("numLefts"));
  }

  @Test
  public void testExportStoryNullPath() throws IOException {
    String defaultPath = "./Go Right!.txt";
    model.playStory("Go Right!");
    model.next(1);
    new ExportStory(null, "Go Right!", false).execute(model);
    assertTrue(new File(defaultPath).delete());
  }

  @Test
  public void testNext() {
    model.playStory("Go Right!");
    new Next("1").execute(model);
    assertEquals("Game over, no choices left.", model.getCurrentChoice());
    model.restart();
    new Next("2").execute(model);
    assertEquals(1, (int) model.getStory("Go Right!").getStatuses().get("numLefts"));
  }

  @Test
  public void testPlayStory() {
    new PlayStory("Go Right!").execute(model);
    assertEquals("Go Right!", model.getCurrentStoryName());
    new PlayStory("Strength!").execute(model);
    assertEquals("Strength!", model.getCurrentStoryName());
  }

  @Test
  public void testQuitStory() {
    model.playStory("Go Right!");
    new QuitStory().execute(model);
    assertNull(model.getCurrentStoryName());
  }

  @Test
  public void testRemoveStory() {
    model.playStory("Go Right!");
    new RemoveStory("Go Right!").execute(model);
    assertNull(model.getCurrentStoryName());
    assertEquals(Collections.singletonList("Strength!"), model.getAllStoryNames());
  }

  @Test
  public void testRestart() {
    model.playStory("Go Right!");
    model.next(0);
    assertEquals("Game over, no choices left.", model.getCurrentChoice());
    new Restart().execute(model);
    assertEquals("Go right(1), Go left(2), or Go straight(3)", model.getCurrentChoice());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testExportStoryBadInput() throws IOException {
    String exportPath = "./res/doesn't Exist/story.txt";
    new ExportStory(exportPath, "Go Right!", false).execute(model);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testImportStoryBadInput() throws IOException {
    String exportPath = "./res/doesn't Exist/story.txt";
    new ImportStory(exportPath).execute(model);
  }

  @Test
  public void testNextConstructor() {
    int numBad = 0;
    List<String> toTry = Arrays.asList("-1", "0", "20", "one", "1", "bad test", null);
    for (String attempt : toTry) {
      try {
        new Next(attempt);
      } catch (IllegalArgumentException e) {
        numBad++;
      }
    }

    assertEquals(5, numBad);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNextBadInput() {
    model.playStory("Go Right!");
    new Next("4").execute(model);
  }

  @Test(expected = IllegalStateException.class)
  public void testNextNoneLoaded() {
    new Next("1").execute(model);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testPlayStoryBadInput() {
    new PlayStory("None").execute(model);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testRemoveStoryBadInput() {
    new RemoveStory("None").execute(model);
  }

  @Test(expected = IllegalStateException.class)
  public void testRestartNoneLoaded() {
    new Restart().execute(model);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testExportStoryNullStory() throws IOException {
    new ExportStory(".", null, false).execute(model);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testImportStoryNullPath() throws IOException {
    new ImportStory(null).execute(model);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testPlayStoryNullStory() {
    new PlayStory(null).execute(model);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testRemoveStoryNullStory() {
    new RemoveStory(null).execute(model);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testExportStoryNullModel() throws IOException {
    new ExportStory("./file.txt", "name", false).execute(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testImportStoryNullModel() throws IOException {
    new ImportStory("./res/controllerTest/story.txt").execute(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNextNullModel() {
    new Next("1").execute(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testPlayStoryNullModel() {
    new PlayStory("name").execute(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testQuitStoryNullModel() {
    new QuitStory().execute(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testRemoveStoryNullModel() {
    new RemoveStory("name").execute(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testRestartNullModel() {
    new Restart().execute(null);
  }
}

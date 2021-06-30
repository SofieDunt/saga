import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import model.SimpleStoryPlayerModel;
import model.StoryPlayerModel;
import model.game.Choice;
import model.game.StoryGame;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for {@link SimpleStoryPlayerModel}s.
 */
public class SimpleStoryPlayerModelTest {

  /**
   * A mock story game that allows for null names to test the model's ability to create valid
   * aliases.
   */
  public static class MockUntitledStory implements StoryGame {

    @Override
    public void next(int decision) throws IllegalArgumentException {

    }

    @Override
    public String getName() {
      return null;
    }

    @Override
    public Choice getCurrentChoice() {
      return null;
    }

    @Override
    public Map<String, Integer> getStatuses() {
      return null;
    }
  }


  private final StoryPlayerModel model = new SimpleStoryPlayerModel();

  @Before
  public void initData() {
    model.addStory(TestDataProvider.goRight());
    model.addStory(TestDataProvider.strengthStory());
  }

  @Test
  public void addStory() {
    assertEquals(new ArrayList<>(Arrays.asList("Go Right!", "Strength!")),
        model.getAllStoryNames());
    // Valid name
    model.addStory(TestDataProvider.goRight());
    assertEquals(new ArrayList<>(Arrays.asList("Go Right!", "Go Right!(1)", "Strength!")),
        model.getAllStoryNames());
    model.addStory(TestDataProvider.goRight());
    assertEquals(
        new ArrayList<>(Arrays.asList("Go Right!", "Go Right!(1)", "Go Right!(2)", "Strength!")),
        model.getAllStoryNames());
    model.addStory(TestDataProvider.goRight());
    assertEquals(new ArrayList<>(
            Arrays.asList("Go Right!", "Go Right!(1)", "Go Right!(2)", "Go Right!(3)", "Strength!")),
        model.getAllStoryNames());
    model.addStory(new MockUntitledStory());
    assertEquals(new ArrayList<>(
            Arrays.asList("Go Right!", "Go Right!(1)", "Go Right!(2)", "Go Right!(3)", "Strength!",
                "Untitled")),
        model.getAllStoryNames());
    model.addStory(new MockUntitledStory());
    assertEquals(new ArrayList<>(
            Arrays.asList("Go Right!", "Go Right!(1)", "Go Right!(2)", "Go Right!(3)", "Strength!",
                "Untitled", "Untitled(1)")),
        model.getAllStoryNames());
  }

  @Test
  public void removeStory() {
    model.removeStory("Go Right!");
    assertEquals(new ArrayList<>(Collections.singletonList("Strength!")), model.getAllStoryNames());
    model.playStory("Strength!");
    model.removeStory("Strength!");
    assertEquals(new ArrayList<>(), model.getAllStoryNames());
    assertNull(model.getCurrentStoryName());
    String msg = "noException";
    try {
      model.removeStory("Not a story");
    } catch (IllegalArgumentException e) {
      msg = e.getMessage();
    }
    assertEquals("No story \"Not a story\" to remove", msg);
    msg = "noException";
    try {
      model.removeStory("Doesn't exist!");
    } catch (IllegalArgumentException e) {
      msg = e.getMessage();
    }
    assertEquals("No story \"Doesn't exist!\" to remove", msg);
    try {
      model.removeStory(null);
    } catch (IllegalArgumentException e) {
      msg = e.getMessage();
    }
    assertEquals("No story \"null\" to remove", msg);
  }

  @Test
  public void playStory() {
    model.playStory("Go Right!");
    assertEquals("Go Right!", model.getCurrentStoryName());
    assertEquals("Go right(1), Go left(2), or Go straight(3)", model.getCurrentChoice());
    model.playStory("Strength!");
    assertEquals("Strength!", model.getCurrentStoryName());
    assertEquals("get strength(1) or don't get strength(2)", model.getCurrentChoice());
    String msg = "noException";
    try {
      model.playStory("None");
    } catch (IllegalArgumentException e) {
      msg = e.getMessage();
    }
    assertEquals("No story \"None\" to load", msg);
    assertEquals("Strength!", model.getCurrentStoryName());
  }

  @Test
  public void next() {
    model.playStory("Go Right!");
    model.next(1);
    assertEquals("Go right(1), Go left(2), or Go straight(3)", model.getCurrentChoice());
    model.next(2);
    assertEquals("Go right(1), Go left(2), or Go straight(3)", model.getCurrentChoice());
    model.next(0);
    assertEquals("Game over, no choices left.", model.getCurrentChoice());
  }

  @Test
  public void nextNoneLoaded() {
    String msg = "noException";
    try {
      model.next(0);
    } catch (IllegalStateException e) {
      msg = e.getMessage();
    }
    assertEquals("No loaded story!", msg);
  }

  @Test
  public void quitStory() {
    assertNull(model.getCurrentStoryName());
    model.playStory("Strength!");
    model.quitStory();
    assertNull(model.getCurrentStoryName());
    // Saved
    model.playStory("Strength!");
    model.next(0);
    model.next(0);
    model.quitStory();
    assertNull(model.getCurrentStoryName());
    model.playStory("Strength!");
    assertEquals("Game over, no choices left.", model.getCurrentChoice());
  }

  @Test
  public void quitStorySavesProgress() {
    // Saved
    model.playStory("Strength!");
    model.next(1);
    model.quitStory();
    assertNull(model.getCurrentStoryName());
    model.playStory("Strength!");
    assertEquals("continue(1) or quit(2)", model.getCurrentChoice());
  }

  @Test
  public void getCurrentChoiceNone() {
    assertNull(model.getCurrentChoice());
  }
}

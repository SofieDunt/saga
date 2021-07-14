import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import model.game.SimpleChoice;
import model.game.SimpleStoryGame;
import model.game.StoryGame;
import org.junit.Test;

/**
 * Tests for {@link SimpleStoryGame}s.
 */
public class SimpleStoryGameTest {

  private final StoryGame goRight = TestDataProvider.goRight();
  private final StoryGame strengthStory = TestDataProvider.strengthStory();

  @Test
  public void testStandardConstructor() {
    Map<String, Integer> map1 = new HashMap<>();
    map1.put("hello", 0);
    map1.put("world", 1);
    StoryGame story = new SimpleStoryGame("story", SimpleChoice.endChoice(), map1);
    assertEquals(2, story.getStatuses().size());
    assertEquals(0, (int) story.getStatuses().get("hello"));
    assertEquals(1, (int) story.getStatuses().get("world"));
    assertNotSame(map1, story.getStatuses());
  }

  @Test
  public void testCopyConstructor() {
    goRight.next(1);
    StoryGame copy = new SimpleStoryGame(goRight);
    assertEquals("Go Right!", copy.getName());
    assertEquals("Go right(1), Go left(2), or Go straight(3)",
        copy.getCurrentChoice().toString());
    assertEquals(1, (int) copy.getStatuses().get("numLefts"));
    assertEquals(0, (int) copy.getOriginalStory().getStatuses().get("numLefts"));
    assertNotSame(goRight, copy);
    assertNotSame(goRight.getStatuses(), copy.getStatuses());
  }

  @Test
  public void testToString() {
    assertEquals("Go Right!", goRight.toString());
  }

  @Test
  public void playThroughGame() {
    // Tests next, getChoiceOptions, and getStatuses by playing through the goRight game
    Random rand = new Random();
    int totalCount = rand.nextInt(100);
    int leftCount = 0;
    int straightCount = 0;

    for (int i = 1; i <= totalCount; i++) {
      if (rand.nextBoolean()) {
        leftCount++;
        goRight.next(1);
      } else {
        straightCount++;
        goRight.next(2);
      }
      assertEquals(leftCount, (int) goRight.getStatuses().get("numLefts"));
      assertEquals(straightCount, (int) goRight.getStatuses().get("numStraights"));
      assertEquals(3, goRight.getCurrentChoice().getOptions().size());
      assertEquals("Go right", goRight.getCurrentChoice().getOptions().get(0).toString());
      assertEquals("Go left", goRight.getCurrentChoice().getOptions().get(1).toString());
      assertEquals("Go straight", goRight.getCurrentChoice().getOptions().get(2).toString());
    }

    goRight.next(0);
    assertEquals(totalCount,
        (int) goRight.getStatuses().get("numLefts") + goRight.getStatuses().get("numStraights"));
    assertEquals(0, goRight.getCurrentChoice().getOptions().size());
  }

  @Test
  public void playThroughDependentGame() {
    strengthStory.next(0);
    assertEquals(1, strengthStory.getCurrentChoice().getOptions().size());
    strengthStory.next(0);
    assertEquals(0, strengthStory.getCurrentChoice().getOptions().size());
  }

  @Test
  public void testBadChoices() {
    String msg = "noException";
    try {
      goRight.next(-1);
    } catch (IllegalArgumentException e) {
      msg = e.getMessage();
    }
    assertEquals("No choice 0", msg);
    msg = "noException";
    try {
      goRight.next(3);
    } catch (IllegalArgumentException e) {
      msg = e.getMessage();
    }
    assertEquals("No choice 4", msg);

    goRight.next(0);
    assertFalse(goRight.next(0));
  }

  @Test
  public void getOriginalStory() {
    StoryGame story = TestDataProvider.strengthStory();
    story.next(0);
    assertEquals("win", story.getCurrentChoice().toString());
    assertEquals(
        "get 1 strength(1), get 2 strength(2), get 3 strength(3), or don't get strength(4)",
        story.getOriginalStory().getCurrentChoice().toString());
    assertEquals(0, (int) story.getOriginalStory().getStatuses().get("strength"));
  }
}

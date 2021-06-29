import static org.junit.Assert.assertEquals;

import java.util.Random;
import model.game.StoryGame;
import org.junit.Test;

public class SimpleStoryGameTest {

  private final StoryGame goRight = TestDataProvider.goRight();
  private final StoryGame strengthStory = TestDataProvider.strengthStory();

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
      assertEquals("Go right", goRight.getCurrentChoice().getOptions().get(0));
      assertEquals("Go left", goRight.getCurrentChoice().getOptions().get(1));
      assertEquals("Go straight", goRight.getCurrentChoice().getOptions().get(2));
    }

    goRight.next(0);
    assertEquals(totalCount,
        (int) goRight.getStatuses().get("numLefts") + goRight.getStatuses().get("numStraights"));
    assertEquals(0, goRight.getCurrentChoice().getOptions().size());
  }

  @Test
  public void playThroughDependentGame() {
    strengthStory.next(0);
    assertEquals(0, strengthStory.getCurrentChoice().getOptions().size());
  }

  @Test
  public void playThroughDependentGame2() {
    strengthStory.next(1);
    assertEquals(2, strengthStory.getCurrentChoice().getOptions().size());
    strengthStory.next(0);
    assertEquals(2, strengthStory.getCurrentChoice().getOptions().size());
    strengthStory.next(1);
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
    assertEquals("No choice -1", msg);
    msg = "noException";
    try {
      goRight.next(3);
    } catch (IllegalArgumentException e) {
      msg = e.getMessage();
    }
    assertEquals("No choice 3", msg);

    goRight.next(0);
    try {
      goRight.next(0);
    } catch (IllegalStateException e) {
      msg = e.getMessage();
    }
    assertEquals("The story has been completed!", msg);
  }
}

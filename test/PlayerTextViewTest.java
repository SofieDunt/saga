import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import model.SimpleStoryPlayerModel;
import model.StoryPlayerModel;
import model.game.StoryGame;
import org.junit.Before;
import org.junit.Test;
import view.LibraryApplicationView;
import view.PlayerTextView;

/**
 * Tests for {@link PlayerTextView}s.
 */
public class PlayerTextViewTest {

  private final StoryPlayerModel<StoryGame> model = new SimpleStoryPlayerModel();
  private final StoryGame goRight = TestDataProvider.goRight();
  private final StoryGame strength = TestDataProvider.strengthStory();
  private final Appendable appendable = new StringBuilder();
  private final LibraryApplicationView view = new PlayerTextView(model, appendable);

  @Before
  public void initData() {
    model.addStory(goRight);
    model.addStory(strength);
  }

  @Test
  public void renderCurrent() throws IOException {
    view.renderCurrent();
    assertEquals("No loaded story", appendable.toString());
    model.playStory("Go Right!");
    view.renderCurrent();
    assertTrue(appendable.toString().contains("Go right(1), Go left(2), or Go straight(3)"));
    model.next(0);
    view.renderCurrent();
    assertTrue(appendable.toString().contains("Game over, no choices left."));
  }

  @Test
  public void renderLibrary() throws IOException {
    view.renderLibrary();
    assertEquals("Go Right!\nStrength!\n", appendable.toString());
    model.playStory("Strength!");
    view.renderLibrary();
    assertTrue(appendable.toString().contains("Go Right!\nStrength!\nLoaded: Strength!"));
    model.removeStory("Go Right!");
    model.removeStory("Strength!");
    view.renderLibrary();
    assertTrue(
        appendable.toString().contains("You don't have any stories in your library. Import some!"));
  }

  @Test
  public void renderMessage() throws IOException {
    view.renderMessage("tes");
    assertEquals("tes", appendable.toString());
    view.renderMessage("t message");
    assertEquals("test message", appendable.toString());
    view.renderMessage("! test \n message ");
    assertEquals("test message! test \n message ", appendable.toString());
    view.renderMessage(null);
    assertEquals("test message! test \n message null", appendable.toString());
  }
}

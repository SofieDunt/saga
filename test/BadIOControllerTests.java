import static org.junit.Assert.assertEquals;

import controller.AbstractController;
import controller.SimpleController;
import java.io.IOException;
import java.io.StringReader;
import java.nio.CharBuffer;
import model.SimpleStoryPlayerModel;
import model.StoryPlayerModel;
import model.game.StoryGame;
import org.junit.Test;
import view.TextView;

/**
 * Tests to ensure failed IO processes are handled correctly by the controller.
 */
public class BadIOControllerTests {

  /**
   * A mock appendable class to mock failed appends on specific calls.
   */
  public static class BadAppendable implements Appendable {

    private final int failOn;
    private int n;

    /**
     * Constructs a faulty appendable that fails on the nth call.
     *
     * @param failOn the nth call to fail on, where n starts at 1.
     */
    public BadAppendable(int failOn) {
      this.failOn = failOn;
      this.n = 1;
    }

    @Override
    public Appendable append(CharSequence csq) throws IOException {
      return failOnNth();
    }

    @Override
    public Appendable append(CharSequence csq, int start, int end) throws IOException {
      return failOnNth();
    }

    @Override
    public Appendable append(char c) throws IOException {
      return failOnNth();
    }

    /**
     * Fails randomly, otherwise returns itself.
     *
     * @return itself, the appendable
     */
    private Appendable failOnNth() throws IOException {
      if (this.n == this.failOn) {
        throw new IOException("Mock fail append");
      } else {
        n++;
        return this;
      }
    }
  }

  /**
   * A mock readable class to mock failed reads.
   */
  public static class BadReadable implements Readable {

    @Override
    public int read(CharBuffer cb) throws IOException {
      throw new IOException("Mock fail read");
    }
  }

  /**
   * A mock controller to mock an abstract controller executing a failed IO command.
   */
  public static class BadController extends AbstractController {

    /**
     * Constructs a controller that throws IOExceptions.
     *
     * @param appendable the appendable to write to
     * @param readable   the readable to read from
     * @throws IllegalArgumentException if any argument is null
     */
    public BadController(Appendable appendable, Readable readable)
        throws IllegalArgumentException {
      super(new TextView(new SimpleStoryPlayerModel(), appendable), readable);
    }

    @Override
    protected void executeBaseCommand(String baseCommand)
        throws IOException, IllegalArgumentException {
      throw new IOException("Mock fail IO in base command");
    }

    @Override
    protected void defaultRender() throws IllegalStateException {

    }
  }

  @Test(expected = IllegalStateException.class)
  public void testFailedRenderMessage() {
    new SimpleController(new SimpleStoryPlayerModel(), new StringReader(""),
        new BadAppendable(1)).play();
  }

  @Test(expected = IllegalStateException.class)
  public void testFailedRenderLibrary() {
    new SimpleController(new SimpleStoryPlayerModel(), new StringReader(""),
        new BadAppendable(3)).play();
  }

  @Test(expected = IllegalStateException.class)
  public void testFailedRenderCurrent() {
    StoryPlayerModel<StoryGame> model = new SimpleStoryPlayerModel();
    model.addStory(TestDataProvider.goRight());
    model.playStory("Go Right!");
    new SimpleController(model, new StringReader(""), new BadAppendable(2)).play();
  }

  @Test
  public void testFailedAppendMessage() {
    try {
      new SimpleController(new SimpleStoryPlayerModel(), new StringReader(""),
          new BadAppendable(1)).play();
    } catch (IllegalStateException e) {
      assertEquals("The view can not be rendered.", e.getMessage());
    }
  }

  @Test
  public void testFailedReadClosesApplication() {
    StringBuilder output = new StringBuilder();
    new SimpleController(new SimpleStoryPlayerModel(), new BadReadable(), output).play();
    assertEquals("Welcome to the story player!\n"
        + "Your story library:\n"
        + "You don't have any stories in your library. Import some!\n"
        + "Enter a valid command:\n"
        + "Application closed.", output.toString());
  }

  @Test
  public void testFailIOBaseCommand() {
    StringBuilder output = new StringBuilder();
    new BadController(output, new StringReader("export")).play();
    assertEquals("Welcome to the story player!\n"
        + "Enter a valid command:\n"
        + "Could not export: IO failure: Mock fail IO in base command\n"
        + "Enter a valid command:\n"
        + "Application closed.", output.toString());
  }
}

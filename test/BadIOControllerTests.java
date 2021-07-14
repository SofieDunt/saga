import static org.junit.Assert.assertEquals;

import controller.AbstractController;
import controller.ApplicationController;
import controller.PlayerController;
import controller.WriterController;
import java.io.IOException;
import java.io.StringReader;
import java.nio.CharBuffer;
import model.SimpleStoryPlayerModel;
import model.SimpleStoryWriterModel;
import model.StoryPlayerModel;
import model.game.StoryGame;
import org.junit.Test;
import view.PlayerTextView;

/**
 * Tests to ensure failed IO processes are handled correctly by the controller.
 */
public abstract class BadIOControllerTests {

  /**
   * Returns an instance of a controller that appends to the given appendable for testing.
   *
   * @param appendable the appendable the controller writes to
   * @return the controller instance
   */
  protected abstract ApplicationController controller(Appendable appendable);

  /**
   * Constructs an instance of a controller that reads input from the given readable for testing.
   *
   * @param readable the readable the controller reads input from
   * @return the controller instance
   */
  protected abstract ApplicationController controller(Readable readable);

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
      super(new PlayerTextView(new SimpleStoryPlayerModel(), appendable), readable);
    }

    @Override
    protected void executeBaseCommand(String baseCommand)
        throws IOException, IllegalArgumentException {
      throw new IOException("Mock fail IO in base command");
    }

    @Override
    protected void defaultRender() throws IllegalStateException {

    }

    @Override
    protected void welcome() throws IllegalStateException {

    }

    @Override
    protected boolean isBaseCommand(String s) {
      return true;
    }
  }

  @Test(expected = IllegalStateException.class)
  public void testFailedRenderMessage() {
    controller(new BadAppendable(1)).play();
  }

  @Test(expected = IllegalStateException.class)
  public void testFailedRenderLibrary() {
    controller(new BadAppendable(3)).play();
  }

  @Test(expected = IllegalStateException.class)
  public void testFailedRenderCurrent() {
    StoryPlayerModel<StoryGame> model = new SimpleStoryPlayerModel();
    model.addStory(TestDataProvider.goRight());
    model.playStory("Go Right!");
    controller(new BadAppendable(2)).play();
  }

  @Test
  public void testFailedAppendMessage() {
    try {
      controller(new BadAppendable(1)).play();
    } catch (IllegalStateException e) {
      assertEquals("The view can not be rendered.", e.getMessage());
    }
  }

  @Test
  public void testFailedReadClosesApplicationPlayer() {
    StringBuilder output = new StringBuilder();
    new PlayerController(new SimpleStoryPlayerModel(), new BadReadable(), output).play();
    assertEquals("Welcome to the story player!\n"
        + "Your story library:\n"
        + "You don't have any stories in your library. Import some!\n"
        + "Enter a valid command:\n"
        + "Application closed.", output.toString());
  }

  @Test
  public void testFailedReadClosesApplicationWriter() {
    StringBuilder output = new StringBuilder();
    new WriterController(new SimpleStoryWriterModel(), new BadReadable(), output).play();
    assertEquals("Welcome to the story creator!\n"
        + "\n"
        + "You don't have any works in your library. Start one now!\n"
        + "Enter a valid command:\n"
        + "Application closed.", output.toString());
  }

  @Test
  public void testFailIOBaseCommand() {
    StringBuilder output = new StringBuilder();
    new BadController(output, new StringReader("export")).play();
    assertEquals("Enter a valid command:\n"
        + "Could not export: IO failure: Mock fail IO in base command\n"
        + "Enter a valid command:\n"
        + "Application closed.", output.toString());
  }
}

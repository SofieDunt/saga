import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import model.SimpleStoryWriterModel;
import model.StoryWriterModel;
import model.game.StoryGame;
import org.junit.Before;
import org.junit.Test;
import view.LibraryApplicationView;
import view.WriterTextView;

public class WriterTextViewTest {

  private final StoryWriterModel<StoryGame> model = new SimpleStoryWriterModel();
  private final Appendable appendable = new StringBuilder();
  private final LibraryApplicationView view = new WriterTextView(model, appendable);

  @Before
  public void initData() {
    model.start("Go Right!");
    model.load("Go Right!");
    model.addStatus("numLefts", 0);
    model.addStatus("numStraights", 0);
    model.addChoice(); // right/left/straight
    model.addChoice(); // end
    model.setInitialChoice(0);
    model.addSimpleDecision("Go right", 0, 1);
    model.addConsequentialDecision("Go left", 0, 0,
        Collections.singletonList("ADD 1 numLefts"));
    model.addConsequentialDecision("Go straight", 0, 0,
        Collections.singletonList("ADD 1 numStraights"));
    model.quit();
  }

  @Test
  public void renderCurrent() throws IOException {
    view.renderCurrent();
    assertEquals("No loaded work", appendable.toString());
    int from = appendable.toString().length();
    model.load("Go Right!");
    view.renderCurrent();
    assertEquals("Name: Go Right!\n"
            + "Statuses:\n"
            + "Name: numLefts, Initial Value: 0\n"
            + "Name: numStraights, Initial Value: 0\n"
            + "Choice #1: Go right(1), Go left(2), or Go straight(3) "
            + "[ Decision #1 Decision #2 Decision #3 ]\n"
            + "Choice #2: Game over, no choices left. [ ]\n"
            + "Decision #1: Go right "
            + "( SIMPLE \"Go right\" Choice #2 )\n"
            + "Decision #2: Go left "
            + "( CONSEQUENTIAL \"Go left\" [ ADD 1 \"numLefts\" ] Choice #1 )\n"
            + "Decision #3: Go straight "
            + "( CONSEQUENTIAL \"Go straight\" [ ADD 1 \"numStraights\" ] Choice #1 )\n",
        appendable.toString().substring(from));
    from = appendable.toString().length();
    model.addChoice();
    model.addSimpleDecision("Test", 2, 1);
    view.renderCurrent();
    assertEquals("Name: Go Right!\n"
            + "Statuses:\n"
            + "Name: numLefts, Initial Value: 0\n"
            + "Name: numStraights, Initial Value: 0\n"
            + "Choice #1: Go right(1), Go left(2), or Go straight(3) "
            + "[ Decision #1 Decision #2 Decision #3 ]\n"
            + "Choice #2: Game over, no choices left. [ ]\n"
            + "Choice #3: Test [ Decision #4 ]\n"
            + "Decision #1: Go right "
            + "( SIMPLE \"Go right\" Choice #2 )\n"
            + "Decision #2: Go left "
            + "( CONSEQUENTIAL \"Go left\" [ ADD 1 \"numLefts\" ] Choice #1 )\n"
            + "Decision #3: Go straight "
            + "( CONSEQUENTIAL \"Go straight\" [ ADD 1 \"numStraights\" ] Choice #1 )\n"
            + "Decision #4: Test ( SIMPLE \"Test\" Choice #2 )\n",
        appendable.toString().substring(from));
    from = appendable.toString().length();
    model.addConsequentialThresholdDecision("Test Complicated", 2, "numLefts", 3, 0, 1,
        Arrays.asList("ADD 1 numLefts", "SET 20 numStraights"));
    view.renderCurrent();
    assertEquals("Name: Go Right!\n"
            + "Statuses:\n"
            + "Name: numLefts, Initial Value: 0\n"
            + "Name: numStraights, Initial Value: 0\n"
            + "Choice #1: Go right(1), Go left(2), or Go straight(3) "
            + "[ Decision #1 Decision #2 Decision #3 ]\n"
            + "Choice #2: Game over, no choices left. [ ]\n"
            + "Choice #3: Test(1) or Test Complicated(2) [ Decision #4 Decision #5 ]\n"
            + "Decision #1: Go right ( SIMPLE \"Go right\" Choice #2 )\n"
            + "Decision #2: Go left "
            + "( CONSEQUENTIAL \"Go left\" [ ADD 1 \"numLefts\" ] Choice #1 )\n"
            + "Decision #3: Go straight "
            + "( CONSEQUENTIAL \"Go straight\" [ ADD 1 \"numStraights\" ] Choice #1 )\n"
            + "Decision #4: Test ( SIMPLE \"Test\" Choice #2 )\n"
            + "Decision #5: Test Complicated "
            + "( DEPENDENT TWOTHRESHOLD \"numLefts\" 3 Choice #1 Choice #2 "
            + "[ CONSEQUENTIAL \"Test Complicated\" "
            + "[ ADD 1 \"numLefts\" | SET 20 \"numStraights\" ] "
            + "Choice #1 ] )\n",
        appendable.toString().substring(from));
  }

  @Test
  public void renderLibrary() throws IOException {
    view.renderLibrary();
    assertEquals("Your library:\n"
        + "Go Right!\n", appendable.toString());
    int from = appendable.toString().length();
    model.start("New story");
    model.load("New story");
    view.renderLibrary();
    assertEquals("Your library:\n"
        + "Go Right!\n"
        + "New story\n"
        + "Loaded: New story", appendable.toString().substring(from));
    from = appendable.toString().length();
    model.remove("Go Right!");
    model.remove("New story");
    view.renderLibrary();
    assertEquals("You don't have any works in your library. Start one now!",
        appendable.toString().substring(from));
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

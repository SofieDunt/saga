import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.HashMap;
import java.util.Map;
import model.game.Choice;
import model.game.SimpleChoice;
import model.game.decision.OutcomeDeterminer;
import model.game.decision.TwoThresholdDeterminer;
import org.junit.Test;

/**
 * Tests for {@link TwoThresholdDeterminer}s.
 */
public class TwoThresholdDeterminerTest {

  @Test
  public void getOutcome() {
    Choice outcome1 = SimpleChoice.endChoice();
    Choice outcome2 = SimpleChoice.endChoice();

    OutcomeDeterminer determiner = new TwoThresholdDeterminer("points", 2, outcome1, outcome2);
    Map<String, Integer> statuses = new HashMap<>();
    String msg = "No exception";
    try {
      determiner.getOutcome(statuses);
    } catch (IllegalArgumentException e) {
      msg = e.getMessage();
    }
    assertEquals("Can't determine outcome of this story - missing status points", msg);

    statuses.put("points", 1);
    assertSame(outcome1, determiner.getOutcome(statuses));
    statuses.replace("points", 2);
    assertSame(outcome2, determiner.getOutcome(statuses));
    statuses.replace("points", 3);
    assertSame(outcome2, determiner.getOutcome(statuses));
  }

  @Test
  public void export() {
    Choice outcome1 = SimpleChoice.endChoice();
    Choice outcome2 = SimpleChoice.endChoice();
    OutcomeDeterminer determiner = new TwoThresholdDeterminer("points", 2, outcome1, outcome2);
    Map<Choice, String> choices = new HashMap<>();

    String msg = "No exception";
    try {
      determiner.export(null);
    } catch (IllegalArgumentException e) {
      msg = e.getMessage();
    }
    assertEquals("Choice representations can't be null", msg);
    try {
      determiner.export(choices);
    } catch (IllegalArgumentException e) {
      msg = e.getMessage();
    }
    assertEquals("Map doesn't contain all outcomes", msg);
    msg = "";
    choices.put(outcome1, "choice1");
    try {
      determiner.export(choices);
    } catch (IllegalArgumentException e) {
      msg = e.getMessage();
    }
    assertEquals("Map doesn't contain all outcomes", msg);
    choices.put(outcome2, "choice2");
    assertEquals("TWOTHRESHOLD \"points\" 2 choice1 choice2", determiner.export(choices));
  }
}

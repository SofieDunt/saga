import static org.junit.Assert.assertEquals;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import model.game.Choice;
import model.game.SimpleChoice;
import model.game.SimpleStoryGame;
import model.game.StoryGame;
import model.game.decision.Decision;
import model.game.decision.DecisionCreator;
import model.game.decision.OutcomeDeterminer;
import model.game.statusUpdate.AddStatus;
import model.game.statusUpdate.StatusUpdate;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for {@link DecisionCreator}.
 */
public class DecisionCreatorTest {

  private Choice outcome;
  private Map<Choice, String> choiceMap;
  private Map<String, StatusUpdate> consequences;

  @Before
  public void initData() {
    outcome = SimpleChoice.endChoice();
    choiceMap = new HashMap<>();
    consequences = new HashMap<>();
  }


  @Test(expected = IllegalArgumentException.class)
  public void importDecisionIllegalFormat1() {
    String decisionStr = "\"test\" C0";
    Scanner sc = new Scanner(new StringReader(decisionStr));
    DecisionCreator.importDecision(sc, Collections.singletonList(outcome));
  }

  @Test(expected = IllegalArgumentException.class)
  public void importDecisionIllegalFormat2() {
    String decisionStr = "BAD \"test\" C0";
    Scanner sc = new Scanner(new StringReader(decisionStr));
    DecisionCreator.importDecision(sc, Collections.singletonList(outcome));
  }

  @Test(expected = IllegalArgumentException.class)
  public void importDecisionNullScanner() {
    DecisionCreator.importDecision(null, new ArrayList<>());
  }

  @Test(expected = IllegalArgumentException.class)
  public void importDecisionNullList() {
    DecisionCreator.importDeterminer(new Scanner(new StringReader("")), null);
  }

  @Test
  public void importSimpleDecision() {
    String decisionStr = "SIMPLE \"test\" C0";
    choiceMap.put(outcome, "C0");
    Scanner sc = new Scanner(new StringReader(decisionStr));
    Decision imported = DecisionCreator.importDecision(sc, Collections.singletonList(outcome));
    assertEquals(Collections.singletonList(outcome), imported.getPossibleOutcomes());
    assertEquals(decisionStr, imported.export(choiceMap));
  }

  @Test(expected = IllegalArgumentException.class)
  public void importSimpleDecisionIllegalFormat1() {
    String decisionStr = "SIMPLE a test C0";
    Scanner sc = new Scanner(new StringReader(decisionStr));
    DecisionCreator.importDecision(sc, Collections.singletonList(outcome));
  }

  @Test(expected = IllegalArgumentException.class)
  public void importSimpleDecisionIllegalFormat2() {
    String decisionStr = "SIMPLE \"test\" C";
    Scanner sc = new Scanner(new StringReader(decisionStr));
    DecisionCreator.importDecision(sc, Collections.singletonList(outcome));
  }

  @Test(expected = IllegalArgumentException.class)
  public void importSimpleDecisionMissingOutcome() {
    String decisionStr = "SIMPLE \"test\" C0";
    Scanner sc = new Scanner(new StringReader(decisionStr));
    DecisionCreator.importDecision(sc, new ArrayList<>());
  }

  @Test
  public void importConsequentialDecision() {
    String decisionStr = "CONSEQUENTIAL \"testC\" [ ADD 3 \"point 2\" | ADD 2 \"point\" ] C0";
    choiceMap.put(outcome, "C0");
    consequences.put("point", new AddStatus(2));
    consequences.put("point 2", new AddStatus(3));

    Map<String, Integer> stats = new HashMap<>();
    stats.put("point", 0);
    stats.put("point 2", -1);
    StoryGame game = new SimpleStoryGame("name", SimpleChoice.endChoice(), stats);

    Scanner sc = new Scanner(new StringReader(decisionStr));
    Decision imported = DecisionCreator.importDecision(sc, Collections.singletonList(outcome));
    assertEquals(Collections.singletonList(outcome), imported.getPossibleOutcomes());
    assertEquals(decisionStr, imported.export(choiceMap));
    imported.makeDecision(game);
    assertEquals(2, (int) game.getStatuses().get("point"));
    assertEquals(2, (int) game.getStatuses().get("point 2"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void importConsequentialDecisionIllegalFormat1() {
    String decisionStr = "CONSEQUENTIAL \"testC\" [ ADD 3 \"point 2\" | ADD 2 \"point\" ]";
    Scanner sc = new Scanner(new StringReader(decisionStr));
    DecisionCreator.importDecision(sc, Collections.singletonList(outcome));
  }

  @Test(expected = IllegalArgumentException.class)
  public void importConsequentialDecisionIllegalFormat2() {
    String decisionStr = "CONSEQUENTIAL \"testC\"  ADD 3 \"point 2\" | ADD 2 \"point\" ]";
    Scanner sc = new Scanner(new StringReader(decisionStr));
    DecisionCreator.importDecision(sc, Collections.singletonList(outcome));
  }

  @Test(expected = IllegalArgumentException.class)
  public void importConsequentialDecisionIllegalFormat3() {
    String decisionStr = "CONSEQUENTIAL \"testC\"  ADD 3 \"point 2\" | ADD 2 \"point\" C0";
    Scanner sc = new Scanner(new StringReader(decisionStr));
    DecisionCreator.importDecision(sc, Collections.singletonList(outcome));
  }

  @Test(expected = IllegalArgumentException.class)
  public void importConsequentialDecisionIllegalFormat4() {
    String decisionStr = "CONSEQUENTIAL \"testC\" [ ADD 3 \"point 2\"  ADD 2 \"point\" ] C0";
    Scanner sc = new Scanner(new StringReader(decisionStr));
    DecisionCreator.importDecision(sc, Collections.singletonList(outcome));
  }

  @Test
  public void importSimpleDependentDecision() {
    String decisionStr = "DEPENDENT TWOTHRESHOLD \"point\" 2 C0 C1 [ SIMPLE \"dependent\" C0 ]";

    Choice outcome2 = SimpleChoice.endChoice();
    choiceMap.put(outcome, "C0");
    choiceMap.put(outcome2, "C1");

    Map<String, Integer> stats = new HashMap<>();
    stats.put("point", 0);
    stats.put("point 2", -1);
    StoryGame game = new SimpleStoryGame("name", SimpleChoice.endChoice(), stats);

    Scanner sc = new Scanner(new StringReader(decisionStr));
    Decision imported = DecisionCreator.importDecision(sc, Arrays.asList(outcome, outcome2));
    assertEquals(Arrays.asList(outcome, outcome2), imported.getPossibleOutcomes());
    assertEquals(decisionStr, imported.export(choiceMap));
    assertEquals(outcome, imported.makeDecision(game));

    game.getStatuses().put("point", 2);
    assertEquals(outcome2, imported.makeDecision(game));
  }

  @Test
  public void importConsequentialDependentDecision() {
    String decisionStr = "DEPENDENT TWOTHRESHOLD \"point\" 2 C0 C1 "
        + "[ CONSEQUENTIAL \"testC\" [ ADD 3 \"point 2\" | ADD 2 \"point\" ] C0 ]";

    Choice outcome2 = SimpleChoice.endChoice();
    choiceMap.put(outcome, "C0");
    choiceMap.put(outcome2, "C1");

    Map<String, Integer> stats = new HashMap<>();
    stats.put("point", -1);
    stats.put("point 2", -1);
    StoryGame game = new SimpleStoryGame("name", SimpleChoice.endChoice(), stats);

    Scanner sc = new Scanner(new StringReader(decisionStr));
    Decision imported = DecisionCreator.importDecision(sc, Arrays.asList(outcome, outcome2));
    assertEquals(Arrays.asList(outcome, outcome2), imported.getPossibleOutcomes());
    assertEquals(decisionStr, imported.export(choiceMap));
    assertEquals(outcome, imported.makeDecision(game));
    assertEquals(1, (int) game.getStatuses().get("point"));
    assertEquals(2, (int) game.getStatuses().get("point 2"));

    assertEquals(outcome2, imported.makeDecision(game));
    assertEquals(3, (int) game.getStatuses().get("point"));
    assertEquals(5, (int) game.getStatuses().get("point 2"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void importDependentDecisionIllegalFormat1() {
    String decisionStr = "DEPENDENT TWOTHRESHOLD 2 C0 C1 "
        + "[ CONSEQUENTIAL \"testC\" [ ADD 3 \"point 2\" | ADD 2 \"point\" ] C0 ]";
    Scanner sc = new Scanner(new StringReader(decisionStr));
    DecisionCreator.importDecision(sc, Collections.singletonList(outcome));
  }

  @Test(expected = IllegalArgumentException.class)
  public void importDependentDecisionIllegalFormat2() {
    String decisionStr = "DEPENDENT TWOTHRESHOLD \"point\" C0 C1 "
        + "[ CONSEQUENTIAL \"testC\" [ ADD 3 \"point 2\" | ADD 2 \"point\" ] C0 ]";
    Scanner sc = new Scanner(new StringReader(decisionStr));
    DecisionCreator.importDecision(sc, Collections.singletonList(outcome));
  }

  @Test(expected = IllegalArgumentException.class)
  public void importDependentDecisionIllegalFormat3() {
    String decisionStr = "DEPENDENT TWOTHRESHOLD \"point\" 2 C C1 "
        + "[ CONSEQUENTIAL \"testC\" [ ADD 3 \"point 2\" | ADD 2 \"point\" ] C0 ]";
    Scanner sc = new Scanner(new StringReader(decisionStr));
    DecisionCreator.importDecision(sc, Collections.singletonList(outcome));
  }

  @Test
  public void importDeterminer() {
    String determinerString = "TWOTHRESHOLD \"point\" 2 C0 C1";
    Scanner sc = new Scanner(new StringReader(determinerString));
    Choice outcome2 = SimpleChoice.endChoice();
    choiceMap.put(outcome, "C0");
    choiceMap.put(outcome2, "C1");
    Map<String, Integer> stats = new HashMap<>();
    stats.put("point", 1);

    OutcomeDeterminer determiner = DecisionCreator
        .importDeterminer(sc, Arrays.asList(outcome, outcome2));

    assertEquals(Arrays.asList(outcome, outcome2), determiner.getPossibleOutcomes());
    assertEquals(outcome, determiner.getOutcome(stats));
    stats.replace("point", 2);
    assertEquals(outcome2, determiner.getOutcome(stats));
    assertEquals(determinerString, determiner.export(choiceMap));
  }

  @Test(expected = IllegalArgumentException.class)
  public void importDeterminerNullScanner() {
    DecisionCreator.importDeterminer(null, new ArrayList<>());
  }

  @Test(expected = IllegalArgumentException.class)
  public void importDeterminerNullList() {
    DecisionCreator.importDeterminer(new Scanner(new StringReader("")), null);
  }
}

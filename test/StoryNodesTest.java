import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import io.StoryNodes;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

/**
 * Tests for {@link StoryNodes}.
 */
public class StoryNodesTest {

  @Test
  public void testCreateNodes() {
    List<String> choiceDescriptions = new ArrayList<>();
    choiceDescriptions.add("[ ]");
    choiceDescriptions.add("[ D0 D1 D2 ]");
    StoryNodes nodes = StoryNodes.createNodes(TestDataProvider.goRight());
    assertEquals("[Go right(1), Go left(2), or Go straight(3), Game over, no choices left.]",
        nodes.getChoices().toString());
    for (String string : nodes.getChoiceOptions().values()) {
      assertTrue(choiceDescriptions.contains(string.substring(3)));
    }
    assertEquals(2, nodes.getChoiceOptions().size());
    assertEquals("[Go right, Go left, Go straight]", nodes.getDecisions().toString());
  }
}

package service.controller;

import static org.testng.Assert.assertEquals;

import service.controller.StoryController;
import service.response.ChoiceResponse;
import service.response.StoryResponse;
import service.response.StoryStatusResponse;
import java.util.Collections;
import java.util.List;
import model.StoryWriterModel;
import model.game.StoryGame;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for {@link StoryController}s.
 */
public class StoryControllerTest {

  private final StoryController controller = new StoryController();

  @Before
  public void init() {
    StoryWriterModel<StoryGame> writerModel = controller.writerModel;
    writerModel.start("Go Right!");
    writerModel.load("Go Right!");
    writerModel.addStatus("numLefts", 0);
    writerModel.addStatus("numStraights", 0);
    writerModel.addChoice(); // right/left/straight
    writerModel.addChoice(); // end
    writerModel.setInitialChoice(0);
    writerModel.addSimpleDecision("Go right", 0, 1);
    writerModel.addConsequentialDecision("Go left", 0, 0,
        Collections.singletonList("ADD 1 numLefts"));
    writerModel.addConsequentialDecision("Go straight", 0, 0,
        Collections.singletonList("ADD 1 numStraights"));
    writerModel.quit();
  }

  @Test
  public void writerStoryResponse() {
    controller.loadWork("Go Right!");
    StoryResponse response = controller.getCurrentWorkAsStory();
    assertEquals("Go Right!", response.getName());

    List<StoryStatusResponse> statuses = response.getStatuses();
    assertEquals("numLefts", statuses.get(0).getName());
    assertEquals("numStraights", statuses.get(1).getName());
    for (StoryStatusResponse status : statuses) {
      assertEquals(0, status.getValue());
    }

    List<ChoiceResponse> choices = response.getChoices();
    assertEquals(0, choices.get(0).getId());
  }
}

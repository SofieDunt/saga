package service.controller;

import java.io.File;
import service.controller.request.AddConsequentialDecisionRequest;
import service.controller.request.AddConsequentialDependentRequest;
import service.controller.request.AddSimpleDecisionRequest;
import service.controller.request.AddSimpleDependentRequest;
import service.response.StoryResponse;
import controller.command.ExportStory;
import controller.command.ExportWork;
import controller.command.ImportStory;
import controller.command.ImportWork;
import io.StoryNodes;
import java.io.IOException;
import java.util.List;
import model.SimpleStoryPlayerModel;
import model.SimpleStoryWriterModel;
import model.StoryPlayerModel;
import model.StoryWriterModel;
import model.game.Choice;
import model.game.StoryGame;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StoryController extends ControllerExceptionHandler {

  StoryPlayerModel<StoryGame> playerModel = new SimpleStoryPlayerModel();
  StoryWriterModel<StoryGame> writerModel = new SimpleStoryWriterModel();

  private static final String PLAYER_BASE = "/player";
  private static final String WRITER_BASE = "/writer";

  private static final String PLAYER_STORE = "store/playStore";
  private static final String WRITER_STORE = "store/writeStore";

  /**
   * Imports any saved files in the store.
   */
  public StoryController() {
    File[] allPlays = new File(PLAYER_STORE).listFiles();
    File[] allWorks = new File(WRITER_STORE).listFiles();

    if (allPlays != null) {
      for (File file : allPlays) {
        try {
          new ImportStory(file.getPath()).execute(playerModel);
        } catch (IllegalArgumentException e) {
          // do nothing
        }
      }
    }

    if (allWorks != null) {
      for (File file : allWorks) {
        try {
          new ImportWork(file.getPath()).execute(writerModel);
        } catch (IllegalArgumentException e) {
          // do nothing
        }
      }
    }
  }

  // Player

  @GetMapping(PLAYER_BASE + "/current/name")
  public String getCurrentStoryName() {
    return playerModel.getCurrentStoryName();
  }

  @GetMapping(PLAYER_BASE + "/current/choice")
  public String getCurrentChoice() {
    return playerModel.getCurrentChoice();
  }

  @GetMapping(PLAYER_BASE + "/current/story")
  public StoryResponse getCurrentStory() {
    String current = playerModel.getCurrentStoryName();
    if (current == null) {
      return null;
    } else {
      StoryGame story = playerModel.getStory(current);
      StoryNodes nodes = StoryNodes.createNodes(story);
      List<Choice> choices = nodes.getChoices();
      return new StoryResponse(
          current,
          story.getStatuses(),
          choices,
          nodes.getDecisions(),
          choices.indexOf(story.getCurrentChoice()));
    }
  }

  @GetMapping(PLAYER_BASE + "/stories")
  public List<String> getAllStoryNames() {
    return playerModel.getAllStoryNames();
  }

  @PostMapping(PLAYER_BASE + "/export")
  public String export(@RequestParam("path") String path, @RequestParam("name") String name)
      throws IOException {
    new ExportStory(path, name, true).execute(playerModel);
    return "Success";
  }

  @PostMapping(PLAYER_BASE + "/export-in-progress")
  public String exportInProgress(@RequestParam("path") String path,
      @RequestParam("name") String name)
      throws IOException {
    new ExportStory(path, name, false).execute(playerModel);
    return "Success";
  }

  @PostMapping(PLAYER_BASE + "/import")
  public void importStory(@RequestParam("path") String path) {
    new ImportStory(path).execute(playerModel);
  }

  @PostMapping(PLAYER_BASE + "/next")
  public void next() {
    playerModel.next(0);
  }

  @PostMapping(PLAYER_BASE + "/choose")
  public void choose(@RequestParam("decision") int decision) {
    playerModel.next(decision);
  }

  @PostMapping(PLAYER_BASE + "/load")
  public void loadStory(@RequestParam("name") String story) {
    playerModel.playStory(story);
  }

  @PostMapping(PLAYER_BASE + "/restart")
  public void restart() {
    playerModel.restart();
  }

  @PostMapping(PLAYER_BASE + "/quit")
  public void quitStory() {
    playerModel.quitStory();
  }

  @DeleteMapping(PLAYER_BASE + "/remove")
  public void removeStory(@RequestParam("name") String name) {
    playerModel.removeStory(name);
  }

  // Writer

  @GetMapping(WRITER_BASE + "/stories")
  public List<String> getAll() {
    return writerModel.getAllWorkNames();
  }

  @GetMapping(WRITER_BASE + "/current/name")
  public String getCurrentWorkName() {
    return writerModel.getCurrentWorkName();
  }


  @GetMapping(WRITER_BASE + "/current/story")
  public StoryResponse getCurrentWorkAsStory() {
    if (writerModel.getCurrentWorkName() == null) {
      return null;
    }
    return new StoryResponse(
        writerModel.getStoryName(),
        writerModel.getStatuses(),
        writerModel.getChoices(),
        writerModel.getDecisions(),
        writerModel.getInitialChoice());
  }

  @GetMapping(WRITER_BASE + "/export")
  public void export(@RequestParam("path") String path) throws IOException {
    new ExportWork(path).execute(writerModel);
  }

  @PostMapping(WRITER_BASE + "/import")
  public void importToWriter(@RequestParam("path") String path) {
    new ImportWork(path).execute(this.writerModel);
  }

  @PostMapping(WRITER_BASE + "/export-to-player")
  public void exportToPlayer() {
    playerModel.addStory(writerModel.create());
  }

  @PostMapping(WRITER_BASE + "/load")
  public void loadWork(@RequestParam("name") String name) {
    writerModel.load(name);
  }

  @PostMapping(WRITER_BASE + "/quit")
  public void quitWork() {
    writerModel.quit();
  }

  @DeleteMapping(WRITER_BASE + "/remove")
  public void removeWork(@RequestParam("name") String name) {
    writerModel.remove(name);
  }

  @PostMapping(WRITER_BASE + "/rename")
  public void rename(@RequestParam("name") String name, @RequestParam("new") String newName) {
    writerModel.rename(name, newName);
  }

  @PostMapping(WRITER_BASE + "/start")
  public void start(@RequestParam("name") String name) {
    writerModel.start(name);
  }

  @PostMapping(WRITER_BASE + "/set/name")
  public void setName(@RequestParam("name") String name) {
    writerModel.setStoryName(name);
  }

  @PostMapping(WRITER_BASE + "/add/status")
  public void addStatus(@RequestParam("name") String name, @RequestParam("val") int val) {
    writerModel.addStatus(name, val);
  }

  @DeleteMapping(WRITER_BASE + "/remove/status")
  public void removeStatus(@RequestParam("name") String name) {
    writerModel.removeStatus(name);
  }

  @PostMapping(WRITER_BASE + "/add/choice")
  public void addChoice() {
    writerModel.addChoice();
  }

  @PostMapping(WRITER_BASE + "/set/initial")
  public void setInitial(@RequestParam("choice") int choice) {
    writerModel.setInitialChoice(choice);
  }

  @PostMapping(WRITER_BASE + "/add/simple-decision")
  public void addSimpleDecision(@RequestBody AddSimpleDecisionRequest request) {
    writerModel.addSimpleDecision(
        request.getDescription(), request.getChoiceId(), request.getOutcomeId());
  }

  @PostMapping(WRITER_BASE + "/add/consequential-decision")
  public void addConsequentialDecision(@RequestBody AddConsequentialDecisionRequest request) {
    writerModel.addConsequentialDecision(request.getDescription(),
        request.getChoiceId(), request.getOutcomeId(), request.getConsequences());
  }

  @PostMapping(WRITER_BASE + "/add/simple-dependent-decision")
  public void addSimpleDependentDecision(@RequestBody AddSimpleDependentRequest request) {
    writerModel.addSimpleDependentThresholdDecision(request.getDescription(), request.getChoiceId(),
        request.getDependency(), request.getThreshold(), request.getOutcomeBelowId(),
        request.getOutcomeMeetsId());
  }

  @PostMapping(WRITER_BASE + "/add/consequential-dependent-decision")
  public void addConsequentialDependentDecision(
      @RequestBody AddConsequentialDependentRequest request) {
    writerModel.addConsequentialThresholdDecision(request.getDescription(), request.getChoiceId(),
        request.getDependency(), request.getThreshold(), request.getOutcomeBelowId(),
        request.getOutcomeMeetsId(), request.getConsequences());
  }

  @DeleteMapping(WRITER_BASE + "/remove/option")
  public void removeOption(@RequestParam("choice") int choice, @RequestParam("opt") int option) {
    writerModel.removeDecision(choice, option);
  }

  @DeleteMapping(WRITER_BASE + "/remove/choice")
  public void removeChoice(@RequestParam("choice") int choice) {
    writerModel.removeChoice(choice);
  }
}

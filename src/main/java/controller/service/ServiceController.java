package controller.service;

import controller.StoryApplicationController;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import model.game.decision.Decision;
import model.game.decision.DecisionCreator;
import controller.service.request.AddConsequentialDecisionRequest;
import controller.service.request.AddConsequentialDependentRequest;
import controller.service.request.AddSimpleDecisionRequest;
import controller.service.request.AddSimpleDependentRequest;
import controller.service.response.ChoiceResponse;
import controller.service.response.DecisionResponse;
import controller.service.response.OptionResponse;
import controller.service.response.StoryResponse;
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
import controller.service.response.StoryStatusResponse;

@RestController
public class ServiceController extends ControllerExceptionHandler implements
    StoryApplicationController {

  StoryPlayerModel<StoryGame> playerModel = new SimpleStoryPlayerModel();
  StoryWriterModel<StoryGame> writerModel = new SimpleStoryWriterModel();

  private static final String PLAYER_BASE = "/player";
  private static final String WRITER_BASE = "/writer";

  private static final String PLAYER_STORE = "store/playStore";
  private static final String WRITER_STORE = "store/writeStore";

  /**
   * Imports any saved files in the store.
   */
  public ServiceController() {
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

  @Override
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

  @Override
  @GetMapping(PLAYER_BASE + "/current/name")
  public String getCurrentStoryName() {
    return playerModel.getCurrentStoryName();
  }

  @Override
  @GetMapping(PLAYER_BASE + "/current/choice")
  public String getCurrentChoice() {
    return playerModel.getCurrentChoice();
  }

  @Override
  @GetMapping(PLAYER_BASE + "/stories")
  public List<String> getAllStoryNames() {
    return playerModel.getAllStoryNames();
  }

  @Override
  @PostMapping(PLAYER_BASE + "/export")
  public void exportStory(@RequestParam("path") String path, @RequestParam("name") String name)
      throws IOException {
    new ExportStory(path, name, true).execute(playerModel);
  }

  @Override
  @PostMapping(PLAYER_BASE + "/export-in-progress")
  public void exportStoryInProgress(@RequestParam("path") String path,
      @RequestParam("name") String name)
      throws IOException {
    new ExportStory(path, name, false).execute(playerModel);
  }

  @Override
  @PostMapping(PLAYER_BASE + "/import")
  public void importStory(@RequestParam("path") String path) {
    new ImportStory(path).execute(playerModel);
  }

  @Override
  @PostMapping(PLAYER_BASE + "/choose")
  public void choose(@RequestParam("decision") int decision) {
    playerModel.next(decision);
  }

  @Override
  @PostMapping(PLAYER_BASE + "/load")
  public void loadStory(@RequestParam("name") String name) {
    playerModel.playStory(name);
  }

  @Override
  @PostMapping(PLAYER_BASE + "/restart")
  public void restartStory() {
    playerModel.restart();
  }

  @Override
  @PostMapping(PLAYER_BASE + "/quit")
  public void quitStory() {
    playerModel.quitStory();
  }

  @Override
  @DeleteMapping(PLAYER_BASE + "/remove")
  public void removeStory(@RequestParam("name") String name) {
    playerModel.removeStory(name);
  }

  // Writer

  @Override
  @GetMapping(WRITER_BASE + "/stories")
  public List<String> getAllWorkNames() {
    return writerModel.getAllWorkNames();
  }

  @Override
  @GetMapping(WRITER_BASE + "/current/story")
  public StoryResponse getCurrentWork() {
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

  @Override
  @GetMapping(WRITER_BASE + "/current/name")
  public String getCurrentWorkName() {
    return writerModel.getCurrentWorkName();
  }

  @Override
  @GetMapping(WRITER_BASE + "/current/story-name")
  public String getCurrentWorkStoryName() {
    return writerModel.getStoryName();
  }

  @Override
  @GetMapping(WRITER_BASE + "/current/statuses")
  public List<StoryStatusResponse> getCurrentWorkStatuses() {
    if (writerModel.getCurrentWorkName() == null) {
      return null;
    }

    List<StoryStatusResponse> statusResponse = new ArrayList<>();
    for (Entry<String, Integer> status : writerModel.getStatuses().entrySet()) {
      statusResponse.add(new StoryStatusResponse(status));
    }
    return statusResponse;
  }

  @Override
  @GetMapping(WRITER_BASE + "/current/choices")
  public List<ChoiceResponse> getCurrentWorkChoices() {
    List<Choice> choices = writerModel.getChoices();
    if (choices == null) {
      return null;
    }
    List<Decision> decisions = writerModel.getDecisions();
    List<ChoiceResponse> choiceResponses = new ArrayList<>();
    for (int i = 0; i < choices.size(); i++) {
      List<Decision> options = choices.get(i).getOptions();
      List<OptionResponse> optionResponses = new ArrayList<>();
      for (int o = 0; o < options.size(); o++) {
        optionResponses.add(new OptionResponse(o, decisions.indexOf(options.get(o))));
      }
      choiceResponses.add(new ChoiceResponse(i, optionResponses));
    }
    return choiceResponses;
  }

  @Override
  @GetMapping(WRITER_BASE + "/current/decisions")
  public List<DecisionResponse> getCurrentWorkDecisions() {
    List<Choice> choices = writerModel.getChoices();
    if (choices == null) {
      return null;
    }
    List<Decision> decisions = writerModel.getDecisions();
    Map<Choice, String> choiceRepresentation = new HashMap<>();
    for (int i = 0; i < choices.size(); i++) {
      choiceRepresentation.put(choices.get(i), "C" + i);
    }

    List<DecisionResponse> decisionResponses = new ArrayList<>();
    for (int i = 0; i < decisions.size(); i++) {
      decisionResponses
          .add(DecisionCreator
              .createDecisionResponse(i, new Scanner(decisions.get(i).export(choiceRepresentation)),
                  choices));
    }
    return decisionResponses;
  }

  @Override
  @PostMapping(WRITER_BASE + "/export")
  public void exportWork(@RequestParam("path") String path) throws IOException {
    new ExportWork(path).execute(writerModel);
  }

  @Override
  @PostMapping(WRITER_BASE + "/export-to-player")
  public void exportToPlayer() {
    playerModel.addStory(writerModel.create());
  }

  @Override
  @PostMapping(WRITER_BASE + "/import")
  public void importToWriter(@RequestParam("path") String path) {
    new ImportWork(path).execute(this.writerModel);
  }

  @Override
  @PostMapping(WRITER_BASE + "/load")
  public void loadWork(@RequestParam("name") String name) {
    writerModel.load(name);
  }

  @Override
  @PostMapping(WRITER_BASE + "/quit")
  public void quitWork() {
    writerModel.quit();
  }

  @Override
  @DeleteMapping(WRITER_BASE + "/remove")
  public void removeWork(@RequestParam("name") String name) {
    writerModel.remove(name);
  }

  @Override
  @PostMapping(WRITER_BASE + "/rename")
  public void renameWork(@RequestParam("name") String name, @RequestParam("new") String newName) {
    writerModel.rename(name, newName);
  }

  @Override
  @PostMapping(WRITER_BASE + "/start")
  public void startNewWork(@RequestParam("name") String name) {
    writerModel.start(name);
  }

  @Override
  @PostMapping(WRITER_BASE + "/set/name")
  public void setStoryName(@RequestParam("name") String name) {
    writerModel.setStoryName(name);
  }

  @Override
  @PostMapping(WRITER_BASE + "/add/status")
  public void addStatus(@RequestParam("name") String name, @RequestParam("val") int val) {
    writerModel.addStatus(name, val);
  }

  @Override
  @DeleteMapping(WRITER_BASE + "/remove/status")
  public void removeStatus(@RequestParam("name") String name) {
    writerModel.removeStatus(name);
  }

  @Override
  @PostMapping(WRITER_BASE + "/add/choice")
  public void addChoice() {
    writerModel.addChoice();
  }

  @Override
  @PostMapping(WRITER_BASE + "/set/initial")
  public void setInitialChoice(@RequestParam("choice") int choice) {
    writerModel.setInitialChoice(choice);
  }

  @Override
  @PostMapping(WRITER_BASE + "/add/simple-decision")
  public void addSimpleDecision(@RequestBody AddSimpleDecisionRequest request) {
    writerModel.addSimpleDecision(
        request.getDescription(), request.getChoiceId(), request.getOutcomeId());
  }

  @Override
  @PostMapping(WRITER_BASE + "/add/consequential-decision")
  public void addConsequentialDecision(@RequestBody AddConsequentialDecisionRequest request) {
    writerModel.addConsequentialDecision(request.getDescription(),
        request.getChoiceId(), request.getOutcomeId(), request.getConsequences());
  }

  @Override
  @PostMapping(WRITER_BASE + "/add/simple-dependent-decision")
  public void addSimpleDependentDecision(@RequestBody AddSimpleDependentRequest request) {
    writerModel.addSimpleDependentThresholdDecision(request.getDescription(), request.getChoiceId(),
        request.getDependency(), request.getThreshold(), request.getOutcomeBelowId(),
        request.getOutcomeMeetsId());
  }

  @Override
  @PostMapping(WRITER_BASE + "/add/consequential-dependent-decision")
  public void addConsequentialDependentDecision(
      @RequestBody AddConsequentialDependentRequest request) {
    writerModel.addConsequentialThresholdDecision(request.getDescription(), request.getChoiceId(),
        request.getDependency(), request.getThreshold(), request.getOutcomeBelowId(),
        request.getOutcomeMeetsId(), request.getConsequences());
  }

  @Override
  @DeleteMapping(WRITER_BASE + "/remove/option")
  public void removeOption(@RequestParam("choice") int choice, @RequestParam("option") int option) {
    writerModel.removeDecision(choice, option);
  }

  @Override
  @DeleteMapping(WRITER_BASE + "/remove/choice")
  public void removeChoice(@RequestParam("choice") int choice) {
    writerModel.removeChoice(choice);
  }
}

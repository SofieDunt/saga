package com.controller;

import com.controller.request.AddConsequentialDecisionRequest;
import com.controller.request.AddConsequentialDependentRequest;
import com.controller.request.AddSimpleDecisionRequest;
import com.controller.request.AddSimpleDependentRequest;
import com.response.ChoiceResponse;
import com.response.DecisionResponse;
import com.response.OptionResponse;
import com.response.StoryResponse;
import com.response.StoryStatusResponse;
import controller.command.ExportStory;
import controller.command.ExportWork;
import controller.command.ImportStory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import model.SimpleStoryPlayerModel;
import model.SimpleStoryWriterModel;
import model.StoryPlayerModel;
import model.StoryWriterModel;
import model.game.Choice;
import model.game.StoryGame;
import model.game.decision.Decision;
import model.game.decision.DecisionCreator;
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

  // JOINT

  @PostMapping(WRITER_BASE + "/export-to-player")
  public String exportToPlayer() {
    playerModel.addStory(writerModel.create());
    return "Success!";
  }

  // Player

  @GetMapping(PLAYER_BASE + "/current-story")
  public String getCurrentStoryName() {
    return playerModel.getCurrentStoryName();
  }

  @GetMapping(PLAYER_BASE + "/current-choice")
  public String getCurrentChoice() {
    return playerModel.getCurrentChoice();
  }

  @GetMapping(PLAYER_BASE + "/stories")
  public List<String> getAllStoryNames() {
    return playerModel.getAllStoryNames();
  }

  @GetMapping(PLAYER_BASE + "/export")
  public String export(@RequestParam("path") String path, @RequestParam("name") String name)
      throws IOException {
    new ExportStory(path, name, true).execute(playerModel);
    return "Success";
  }

  @GetMapping(PLAYER_BASE + "/export-in-progress")
  public String exportInProgress(@RequestParam("path") String path,
      @RequestParam("name") String name)
      throws IOException {
    new ExportStory(path, name, false).execute(playerModel);
    return "Success";
  }

  @PostMapping(PLAYER_BASE + "/import")
  public String importStory(@RequestParam("path") String path) {
    new ImportStory(path).execute(playerModel);
    return "Success";
  }

  @PostMapping(PLAYER_BASE + "/next")
  public String next() {
    playerModel.next(0);
    return playerModel.getCurrentChoice();
  }

  @PostMapping(PLAYER_BASE + "/choose")
  public String choose(@RequestParam("decision") int decision) {
    playerModel.next(decision);
    return playerModel.getCurrentChoice();
  }

  @PostMapping(PLAYER_BASE + "/play")
  public String play(@RequestParam("name") String story) {
    playerModel.playStory(story);
    return playerModel.getCurrentStoryName();
  }

  @PostMapping(PLAYER_BASE + "/restart")
  public String restart() {
    playerModel.restart();
    return playerModel.getCurrentChoice();
  }

  @PostMapping(PLAYER_BASE + "/quit")
  public String quitStory() {
    playerModel.quitStory();
    return "Success";
  }

  @PostMapping(PLAYER_BASE + "/remove")
  public List<String> removeStory(@RequestParam("name") String name) {
    playerModel.removeStory(name);
    return playerModel.getAllStoryNames();
  }

  // Writer

  @GetMapping(WRITER_BASE + "/stories")
  public List<String> getAll() {
    return writerModel.getAllWorkNames();
  }

  @GetMapping(WRITER_BASE + "/current/work-name")
  public String getCurrentWorkName() {
    return writerModel.getCurrentWorkName();
  }

  @GetMapping(WRITER_BASE + "/current/choices")
  public List<StoryStatusResponse> getCurrentStatuses() {
    Map<String, Integer> statuses = writerModel.getStatuses();
    List<StoryStatusResponse> response = new ArrayList<>();
    for (Entry<String, Integer> status : statuses.entrySet()) {
      response.add(new StoryStatusResponse(status));
    }
    return response;
  }

  @GetMapping(WRITER_BASE + "/current/story")
  public StoryResponse getCurrentStory() {
    if (writerModel.getCurrentWorkName() == null) {
      return null;
    }
    List<StoryStatusResponse> statusResponse = new ArrayList<>();
    Map<String, Integer> statuses = writerModel.getStatuses();
    for (Entry<String, Integer> status : statuses.entrySet()) {
      statusResponse.add(new StoryStatusResponse(status));
    }
    List<Choice> choices = writerModel.getChoices();
    List<Decision> decisions = writerModel.getDecisions();

    List<ChoiceResponse> choiceResponses = new ArrayList<>();
    Map<Choice, String> choiceRepresentation = new HashMap<>();
    for (int i = 0; i < choices.size(); i++) {
      List<Decision> options = choices.get(i).getOptions();
      List<OptionResponse> optionResponses = new ArrayList<>();
      for (int o = 0; o < options.size(); o++) {
        optionResponses.add(new OptionResponse(o, decisions.indexOf(options.get(o))));
      }
      choiceResponses.add(new ChoiceResponse(i, optionResponses));
      choiceRepresentation.put(choices.get(i), "C" + i);
    }

    List<DecisionResponse> decisionResponses = new ArrayList<>();
    for (int i = 0; i < decisions.size(); i++) {
      decisionResponses
          .add(DecisionCreator
              .createDecisionResponse(i, new Scanner(decisions.get(i).export(choiceRepresentation)),
                  choices));
    }

    return new StoryResponse(
        writerModel.getStoryName(),
        statusResponse,
        choiceResponses,
        decisionResponses,
        writerModel.getInitialChoice());
  }

  @GetMapping(WRITER_BASE + "export")
  public void export(@RequestParam("path") String path) throws IOException {
    new ExportWork(path).execute(writerModel);
  }

  @PostMapping(WRITER_BASE + "/load")
  public void load(@RequestParam("name") String name) {
    writerModel.load(name);
  }

  @PostMapping(WRITER_BASE + "/quit")
  public void quitWork() {
    writerModel.quit();
  }

  @PostMapping(WRITER_BASE + "/remove")
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

  @PostMapping(WRITER_BASE + "/remove/status")
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

  @PostMapping(WRITER_BASE + "/remove/option")
  public void removeOption(@RequestParam("choice") int choice, @RequestParam("opt") int option) {
    writerModel.removeDecision(choice, option);
  }

  @PostMapping(WRITER_BASE + "/remove/choice")
  public void removeChoice(@RequestParam("choice") int choice) {
    writerModel.removeChoice(choice);
  }
}

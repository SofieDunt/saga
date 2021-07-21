package controller.service.response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import model.game.Choice;
import model.game.decision.Decision;
import model.game.decision.DecisionCreator;

/**
 * Represents the body of the response sent to the client to represent a story.
 */
public class StoryResponse {

  private String name;
  private List<StoryStatusResponse> statuses;
  private List<ChoiceResponse> choices;
  private List<DecisionResponse> decisions;
  private int choice;

  public StoryResponse(String name, List<StoryStatusResponse> statuses,
      List<ChoiceResponse> choices, List<DecisionResponse> decisions, int choice) {
    this.name = name;
    this.statuses = statuses;
    this.choices = choices;
    this.decisions = decisions;
    this.choice = choice;
  }

  /**
   * Constructs a story response from the given information.
   *
   * @param name          the name of the story
   * @param statuses      the story statuses
   * @param choices       the list of choices in the story
   * @param decisions     the list of decisions in the story
   * @param currentChoice the current choice in the story
   * @throws IllegalArgumentException if the given information together does not make a valid story
   *                                  (ex. choice option not in list of decisions)
   */
  public StoryResponse(String name, Map<String, Integer> statuses,
      List<Choice> choices, List<Decision> decisions, int currentChoice)
      throws IllegalArgumentException {
    List<StoryStatusResponse> statusResponse = new ArrayList<>();
    for (Entry<String, Integer> status : statuses.entrySet()) {
      statusResponse.add(new StoryStatusResponse(status));
    }
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

    this.name = name;
    this.statuses = statusResponse;
    this.choices = choiceResponses;
    this.decisions = decisionResponses;
    this.choice = currentChoice;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<StoryStatusResponse> getStatuses() {
    return statuses;
  }

  public void setStatuses(List<StoryStatusResponse> statuses) {
    this.statuses = statuses;
  }

  public List<ChoiceResponse> getChoices() {
    return choices;
  }

  public void setChoices(List<ChoiceResponse> choices) {
    this.choices = choices;
  }

  public List<DecisionResponse> getDecisions() {
    return decisions;
  }

  public void setDecisions(List<DecisionResponse> decisions) {
    this.decisions = decisions;
  }

  public int getChoice() {
    return choice;
  }

  public void setChoice(int choice) {
    this.choice = choice;
  }
}

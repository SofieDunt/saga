package com.response;

import java.util.List;

/**
 * Represents the body of the response sent to the client to represent a story.
 */
public class StoryResponse {

  private String name;
  private List<StoryStatusResponse> statuses;
  private List<ChoiceResponse> choices;
  private List<DecisionResponse> decisions;
  private int initialChoice;

  public StoryResponse(String name, List<StoryStatusResponse> statuses,
      List<ChoiceResponse> choices, List<DecisionResponse> decisions, int initialChoice) {
    this.name = name;
    this.statuses = statuses;
    this.choices = choices;
    this.decisions = decisions;
    this.initialChoice = initialChoice;
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

  public int getInitialChoice() {
    return initialChoice;
  }

  public void setInitialChoice(int initialChoice) {
    this.initialChoice = initialChoice;
  }
}

package com.controller.request;

/**
 * Represents the body of a request to add a simple decision in the story writer.
 */
public class AddSimpleDecisionRequest {

  private String description;
  private int choiceId;
  private int outcomeId;

  public AddSimpleDecisionRequest(String description, int choiceId, int outcomeId) {
    this.description = description;
    this.choiceId = choiceId;
    this.outcomeId = outcomeId;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public int getChoiceId() {
    return choiceId;
  }

  public void setChoiceId(int id) {
    this.choiceId = id;
  }

  public int getOutcomeId() {
    return outcomeId;
  }

  public void setOutcomeId(int outcomeId) {
    this.outcomeId = outcomeId;
  }
}

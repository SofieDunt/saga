package com.controller.request;

import java.util.List;

/**
 * Represents the body of a request to add a consequential decision in the story writer.
 */
public class AddConsequentialDecisionRequest extends AddSimpleDecisionRequest {

  private List<String> consequences;

  public AddConsequentialDecisionRequest(String description, int choiceId, int outcomeId,
      List<String> consequences) {
    super(description, choiceId, outcomeId);
    this.consequences = consequences;
  }

  public List<String> getConsequences() {
    return consequences;
  }

  public void setConsequences(List<String> consequences) {
    this.consequences = consequences;
  }
}

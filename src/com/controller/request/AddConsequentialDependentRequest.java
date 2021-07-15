package com.controller.request;

import java.util.List;

/**
 * Represents the body of a request to add a consequential dependent decision in the story writer.
 */
public class AddConsequentialDependentRequest extends AddSimpleDependentRequest {

  private List<String> consequences;

  public AddConsequentialDependentRequest(String description, int choiceId, String dependency,
      int threshold, int outcomeBelowId, int outcomeMeetsId, List<String> consequences) {
    super(description, choiceId, dependency, threshold, outcomeBelowId, outcomeMeetsId);
    this.consequences = consequences;
  }

  public List<String> getConsequences() {
    return consequences;
  }

  public void setConsequences(List<String> consequences) {
    this.consequences = consequences;
  }
}

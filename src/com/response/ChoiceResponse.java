package com.response;

import java.util.List;

/**
 * Represents the body of the response sent to the client to represent a choice.
 */
public class ChoiceResponse {

  private int choiceId;
  private List<OptionResponse> options;

  public ChoiceResponse(int choiceId, List<OptionResponse> options) {
    this.choiceId = choiceId;
    this.options = options;
  }

  public int getChoiceId() {
    return choiceId;
  }

  public void setChoiceId(int choiceId) {
    this.choiceId = choiceId;
  }

  public List<OptionResponse> getOptions() {
    return options;
  }

  public void setOptions(List<OptionResponse> options) {
    this.options = options;
  }
}

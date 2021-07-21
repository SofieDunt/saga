package controller.service.response;

import java.util.List;

/**
 * Represents the body of the response sent to the client to represent a choice.
 */
public class ChoiceResponse {

  private int id;
  private List<OptionResponse> options;

  public ChoiceResponse(int id, List<OptionResponse> options) {
    this.id = id;
    this.options = options;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public List<OptionResponse> getOptions() {
    return options;
  }

  public void setOptions(List<OptionResponse> options) {
    this.options = options;
  }
}

package service.controller.request;

/**
 * Represents the body of a request to add a simple dependent decision in the story writer.
 */
public class AddSimpleDependentRequest {

  private String description;
  private int choiceId;
  private String dependency;
  private int threshold;
  private int outcomeBelowId;
  private int outcomeMeetsId;

  public AddSimpleDependentRequest(String description, int choiceId, String dependency,
      int threshold, int outcomeBelowId, int outcomeMeetsId) {
    this.description = description;
    this.choiceId = choiceId;
    this.dependency = dependency;
    this.threshold = threshold;
    this.outcomeBelowId = outcomeBelowId;
    this.outcomeMeetsId = outcomeMeetsId;
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

  public void setChoiceId(int choiceId) {
    this.choiceId = choiceId;
  }

  public String getDependency() {
    return dependency;
  }

  public void setDependency(String dependency) {
    this.dependency = dependency;
  }

  public int getThreshold() {
    return threshold;
  }

  public void setThreshold(int threshold) {
    this.threshold = threshold;
  }

  public int getOutcomeBelowId() {
    return outcomeBelowId;
  }

  public void setOutcomeBelowId(int outcomeBelowId) {
    this.outcomeBelowId = outcomeBelowId;
  }

  public int getOutcomeMeetsId() {
    return outcomeMeetsId;
  }

  public void setOutcomeMeetsId(int outcomeMeetsId) {
    this.outcomeMeetsId = outcomeMeetsId;
  }
}

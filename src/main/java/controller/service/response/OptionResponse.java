package controller.service.response;

/**
 * Represents the body of the response sent to the client to represent an option of a choice.
 */
public class OptionResponse {

  private int id;
  private int decision;

  public OptionResponse(int id, int decision) {
    this.id = id;
    this.decision = decision;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getDecision() {
    return decision;
  }

  public void setDecision(int decision) {
    this.decision = decision;
  }
}

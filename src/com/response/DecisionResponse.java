package com.response;

import java.util.ArrayList;
import java.util.List;
import model.game.decision.DecisionTypes;

/**
 * Represents the body of the response sent to the client to represent a decision.
 */
public class DecisionResponse {

  private int id;
  private DecisionTypes type;
  private String description;
  private String dependency;
  private int threshold;
  private int outcome1Id;
  private int outcome2Id;
  private List<StatusUpdateResponse> consequences;

  public DecisionResponse(int id, DecisionTypes type) {
    this.id = id;
    this.type = type;

    // Default none
    this.consequences = new ArrayList<>();
    this.threshold = 0;
    this.outcome2Id = -1;
  }

  public DecisionResponse(int id, DecisionTypes type, String description, int outcomeId) {
    this(id, type);
    this.description = description;
    this.outcome1Id = outcomeId;
  }

  public DecisionResponse(int id, DecisionTypes type, String description, int outcomeId,
      List<StatusUpdateResponse> consequences) {
    this(id, type, description, outcomeId);
    this.consequences = consequences;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public DecisionTypes getType() {
    return type;
  }

  public void setType(DecisionTypes type) {
    this.type = type;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
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

  public int getOutcome1Id() {
    return outcome1Id;
  }

  public void setOutcome1Id(int outcome1Id) {
    this.outcome1Id = outcome1Id;
  }

  public int getOutcome2Id() {
    return outcome2Id;
  }

  public void setOutcome2Id(int outcome2Id) {
    this.outcome2Id = outcome2Id;
  }

  public List<StatusUpdateResponse> getConsequences() {
    return consequences;
  }

  public void setConsequences(List<StatusUpdateResponse> consequences) {
    this.consequences = consequences;
  }
}

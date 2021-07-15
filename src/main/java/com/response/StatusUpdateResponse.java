package com.response;

import model.game.statusUpdate.StatusUpdateTypes;

/**
 * Represents the body of the response sent to the client to represent a status update.
 */
public class StatusUpdateResponse {

  private StatusUpdateTypes type;
  private int var;
  private String status;

  public StatusUpdateResponse(StatusUpdateTypes type, int var, String status) {
    this.type = type;
    this.var = var;
    this.status = status;
  }

  public StatusUpdateTypes getType() {
    return type;
  }

  public void setType(StatusUpdateTypes type) {
    this.type = type;
  }

  public int getVar() {
    return var;
  }

  public void setVar(int var) {
    this.var = var;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }
}

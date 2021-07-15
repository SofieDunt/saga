package com.response;

import java.util.Map.Entry;

/**
 * Represents the body of the response sent to the client to represent a story status.
 */
public class StoryStatusResponse {

  private String name;
  private int initialValue;

  public StoryStatusResponse(String name, int initialValue) {
    this.name = name;
    this.initialValue = initialValue;
  }

  public StoryStatusResponse(Entry<String, Integer> entry) {
    this.name = entry.getKey();
    this.initialValue = entry.getValue();
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getInitialValue() {
    return initialValue;
  }

  public void setInitialValue(int initialValue) {
    this.initialValue = initialValue;
  }
}

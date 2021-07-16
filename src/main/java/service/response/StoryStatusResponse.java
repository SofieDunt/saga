package service.response;

import java.util.Map.Entry;

/**
 * Represents the body of the response sent to the client to represent a story status.
 */
public class StoryStatusResponse {

  private String name;
  private int value;

  public StoryStatusResponse(String name, int value) {
    this.name = name;
    this.value = value;
  }

  public StoryStatusResponse(Entry<String, Integer> entry) {
    this.name = entry.getKey();
    this.value = entry.getValue();
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getValue() {
    return value;
  }

  public void setValue(int value) {
    this.value = value;
  }
}

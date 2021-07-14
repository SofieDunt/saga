package view;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import model.StoryWriterModelState;
import model.game.Choice;
import model.game.decision.Decision;
import utils.Utils;

/**
 * Represents a textual view of a story writer application, where the user's work library and
 * information about the user's current work can be displayed.
 */
public class WriterTextView implements LibraryApplicationView {

  private final Appendable destination;
  private final StoryWriterModelState modelState;

  /**
   * Constructs a {@code WriterTextView} that renders information about the given model state to the
   * given destination.
   *
   * @param modelState  the state of the model
   * @param destination the destination to render to
   * @throws IllegalArgumentException if the destination is null
   */
  public WriterTextView(StoryWriterModelState modelState, Appendable destination)
      throws IllegalArgumentException {
    Utils.ensureNotNull(modelState, "Model state can't be null");
    Utils.ensureNotNull(destination, "Destination can't be null");
    this.modelState = modelState;
    this.destination = destination;
  }

  @Override
  public void renderCurrent() throws IOException {
    String current = this.modelState.getCurrentWorkName();
    if (current == null) {
      this.destination.append("No loaded work");
    } else {
      this.destination.append("Work Name: ").append(current).append("\n");
      this.destination.append("Story Name: ").append(this.modelState.getStoryName()).append("\n");
      this.destination.append("Statuses:\n");
      for (Entry<String, Integer> status : this.modelState.getStatuses().entrySet()) {
        this.destination.append("Name: ").append(status.getKey())
            .append(", Initial Value: ").append(Integer.toString(status.getValue())).append("\n");
      }
      List<Choice> choices = this.modelState.getChoices();
      List<Decision> decisions = this.modelState.getDecisions();
      Map<Choice, String> choiceRepresentations = new HashMap<>();
      for (int i = 0; i < choices.size(); i++) {
        this.destination.append("Choice #").append(getUserFriendly(i)).append(": ")
            .append(choices.get(i).toString()).append(" [ ");
        choiceRepresentations.put(choices.get(i), "Choice #" + getUserFriendly(i));
        for (Decision option : choices.get(i).getOptions()) {
          this.destination.append("Decision #").append(getUserFriendly(decisions.indexOf(option)))
              .append(" ");
        }
        this.destination.append("]\n");
      }
      for (int i = 0; i < decisions.size(); i++) {
        this.destination.append("Decision #").append(getUserFriendly(i)).append(": ")
            .append(decisions.get(i).toString()).append(" ( ")
            .append(decisions.get(i).export(choiceRepresentations)).append(" )\n");
      }
    }
  }

  @Override
  public void renderLibrary() throws IOException {
    List<String> storyNames = this.modelState.getAllWorkNames();
    if (storyNames.size() == 0) {
      this.destination.append("You don't have any works in your library. Start one now!");
    } else {
      this.destination.append("Your library:\n");
      for (String name : storyNames) {
        this.destination.append(name).append("\n");
      }
      String loaded = this.modelState.getCurrentWorkName();
      if (loaded != null) {
        this.destination.append("Loaded: ").append(loaded);
      }
    }
  }

  @Override
  public void renderMessage(String message) throws IOException {
    this.destination.append(message);
  }

  /**
   * Returns the user-friendly index of the given index as a string.
   *
   * @param i the index
   * @return the user-friendly index
   */
  private String getUserFriendly(int i) {
    return Integer.toString((i + 1));
  }
}

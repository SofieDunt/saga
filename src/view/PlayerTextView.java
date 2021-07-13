package view;

import java.io.IOException;
import java.util.List;
import model.StoryPlayerModelState;
import utils.Utils;

/**
 * Represents a textual view of a story player application, where the user's story library and
 * current story interactions, as the current story choice, can be displayed.
 */
public class PlayerTextView implements LibraryApplicationView {

  private final Appendable destination;
  private final StoryPlayerModelState<?> modelState;

  /**
   * Constructs a {@code PlayerTextView} that renders information about the given model state to the
   * given destination.
   *
   * @param modelState  the state of the model
   * @param destination the destination to render to
   * @throws IllegalArgumentException if the destination is null
   */
  public PlayerTextView(StoryPlayerModelState<?> modelState, Appendable destination)
      throws IllegalArgumentException {
    Utils.ensureNotNull(modelState, "Model state can't be null");
    Utils.ensureNotNull(destination, "Destination can't be null");
    this.modelState = modelState;
    this.destination = destination;
  }

  @Override
  public void renderCurrent() throws IOException {
    Object current = this.modelState.getCurrentChoice();
    if (current == null) {
      this.destination.append("No loaded story");
    } else {
      this.destination.append(this.modelState.getCurrentChoice());
    }
  }

  @Override
  public void renderLibrary() throws IOException {
    List<String> storyNames = this.modelState.getAllStoryNames();
    if (storyNames.size() == 0) {
      this.destination.append("You don't have any stories in your library. Import some!");
    } else {
      for (String name : storyNames) {
        this.destination.append(name).append("\n");
      }
      String loaded = this.modelState.getCurrentStoryName();
      if (loaded != null) {
        this.destination.append("Loaded: ").append(loaded);
      }
    }
  }

  @Override
  public void renderMessage(String message) throws IOException {
    this.destination.append(message);
  }
}

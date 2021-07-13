package controller;

import java.io.IOException;
import model.StoryPlayerModel;
import model.game.StoryGame;
import view.StoryPlayerView;
import view.TextView;

/**
 * Represents a controller for an application that allows users to play stories by controlling a
 * {@link StoryPlayerModel} and a {@link StoryPlayerView}.
 */
public class SimpleController extends AbstractController {

  // The model the controller controls
  private final StoryPlayerModel<StoryGame> model;

  /**
   * Constructs a {@code SimpleController} that controls the given model, outputs to a text view
   * that writes to the given appendable, and reads input from the given readable.
   *
   * @param model      the model
   * @param appendable the appendable to output to
   * @param readable   the readable to read from
   * @throws IllegalArgumentException if any argument is null
   */
  public SimpleController(StoryPlayerModel<StoryGame> model, Readable readable,
      Appendable appendable)
      throws IllegalArgumentException {
    super(new TextView(model, appendable), readable);
    this.model = model;
  }

  @Override
  protected void executeBaseCommand(String baseCommand)
      throws IOException, IllegalArgumentException {
    if (knownCommands.containsKey(baseCommand)) {
      knownCommands.get(baseCommand).get().execute(this.model);
    } else {
      ioCommands.get(baseCommand).get().execute(this.model);
      tryRenderMessage("SUCCESS: " + baseCommand + "\n");
    }
  }

  @Override
  protected void defaultRender() throws IllegalStateException {
    // If a story is loaded, render the next choice. Otherwise, render the library.
    if (this.model.getCurrentStoryName() != null) {
      tryRenderCurrent();
    } else {
      tryRenderLibrary();
    }
  }
}

package controller;

import controller.AbstractController;
import controller.command.Command;
import controller.command.ExportStory;
import controller.command.IOCommand;
import controller.command.ImportStory;
import controller.command.Next;
import controller.command.PlayStory;
import controller.command.QuitStory;
import controller.command.RemoveStory;
import controller.command.Restart;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import model.StoryPlayerModel;
import model.game.StoryGame;
import utils.Utils;
import view.StoryPlayerView;
import view.TextView;

/**
 * Represents a controller for an application that allows users to play stories by controlling a
 * {@link StoryPlayerModel} and a {@link StoryPlayerView}. Supports importing, exporting, and
 * playing through, restarting, quitting, and removing stories.
 */
public class PlayerController extends AbstractController {

  // The model the controller controls
  private final StoryPlayerModel<StoryGame> model;
  // The map of known model commands
  protected final Map<String, Supplier<Command<StoryPlayerModel<?>>>> knownCommands;
  // The map of known IO commands
  protected final Map<String, Supplier<IOCommand<StoryPlayerModel<StoryGame>>>> ioCommands;

  /**
   * Constructs a {@code SimpleController} that controls the given model, outputs to a text view
   * that writes to the given appendable, and reads input from the given readable.
   *
   * @param model      the model
   * @param appendable the appendable to output to
   * @param readable   the readable to read from
   * @throws IllegalArgumentException if any argument is null
   */
  public PlayerController(StoryPlayerModel<StoryGame> model, Readable readable,
      Appendable appendable)
      throws IllegalArgumentException {
    super(new TextView(model, appendable), readable);
    this.model = model;
    this.knownCommands = new HashMap<>();
    this.ioCommands = new HashMap<>();
    addCommands();
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

  @Override
  protected boolean isBaseCommand(String s) {
    return this.knownCommands.containsKey(s) || this.ioCommands.containsKey(s) || super
        .isBaseCommand(s);
  }

  /**
   * Adds commands to the controller's map of commands.
   */
  private void addCommands() {
    // ------ Model Commands ------ //
    // Suppliers handle getting appropriate command specifications, ex. layer name
    knownCommands.put("next", () -> new Next("1"));
    knownCommands.put("choose", () ->
        new Next(
            getNextValidInput(true, "Enter a numeric decision:", Utils::isPositiveStringNumber)
        ));
    knownCommands.put("play", () ->
        new PlayStory(
            getNextInput("Enter story name:", true)
        ));
    knownCommands.put("quit", QuitStory::new);
    knownCommands.put("remove", () ->
        new RemoveStory(
            getNextInput("Enter story name:", true)
        ));
    knownCommands.put("restart", Restart::new);
    // ------ IO Commands ------ //
    ioCommands.put("import", () ->
        new ImportStory(
            getNextInput("Enter file path:", true)
        ));
    ioCommands.put("export", () ->
        new ExportStory(
            getNextInput("Enter path to save story to:", true),
            getNextInput("Enter name of story to export:", true),
            true
        ));
    ioCommands.put("exportInProgress", () ->
        new ExportStory(
            getNextInput("Enter path to save story to:", true),
            getNextInput("Enter name of story to export:", true),
            false
        ));
  }
}

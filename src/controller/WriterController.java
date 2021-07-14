package controller;

import controller.command.AddChoice;
import controller.command.AddConsequentialDecision;
import controller.command.AddConsequentialDependentDecision;
import controller.command.AddSimpleDecision;
import controller.command.AddSimpleDependentDecision;
import controller.command.AddStatusToWork;
import controller.command.Command;
import controller.command.ExportWork;
import controller.command.IOCommand;
import controller.command.LoadWork;
import controller.command.QuitWork;
import controller.command.RemoveChoice;
import controller.command.RemoveDecision;
import controller.command.RemoveStatus;
import controller.command.RemoveWork;
import controller.command.RenameWork;
import controller.command.SetInitialChoice;
import controller.command.SetStoryName;
import controller.command.StartWork;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import model.StoryWriterModel;
import model.game.StoryGame;
import utils.Utils;
import view.LibraryApplicationView;
import view.WriterTextView;

/**
 * Represents a controller for an application that allows users to write, or create, stories by
 * controlling a {@link StoryWriterModel} and a {@link LibraryApplicationView}. Supports starting,
 * editing, creating, and exporting stories.
 */
public class WriterController extends AbstractController {

  // The model the controller controls
  private final StoryWriterModel<StoryGame> model;
  // The map of known model commands
  protected final Map<String, Supplier<Command<StoryWriterModel<?>>>> knownCommands;
  // The map of known IO commands
  protected final Map<String, Supplier<IOCommand<StoryWriterModel<StoryGame>>>> ioCommands;

  /**
   * Constructs a {@code WriterController} that controls the given model, outputs to a text view
   * that writes to the given appendable, and reads input from the given readable.
   *
   * @param model      the model
   * @param appendable the appendable to output to
   * @param readable   the readable to read from
   * @throws IllegalArgumentException if any argument is null
   */
  public WriterController(StoryWriterModel<StoryGame> model, Readable readable,
      Appendable appendable)
      throws IllegalArgumentException {
    super(new WriterTextView(model, appendable), readable);
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
    tryRenderMessage("\n");
    // If a work is loaded, render the current work. Otherwise, render the library.
    if (this.model.getCurrentWorkName() != null) {
      tryRenderCurrent();
    } else {
      tryRenderLibrary();
    }
  }

  @Override
  protected void welcome() throws IllegalStateException {
    tryRenderMessage("Welcome to the story creator!\n");
  }

  @Override
  protected boolean isBaseCommand(String s) {
    return this.knownCommands.containsKey(s) || this.ioCommands.containsKey(s) || super
        .isBaseCommand(s);
  }

  /**
   * Adds commands to the controller's maps of commands.
   */
  private void addCommands() {
    // ------ Model Commands ------ //
    // Suppliers handle getting appropriate command specifications
    knownCommands.put("load", () -> new LoadWork(
        getNextInput("Enter work name:", true)
    ));
    knownCommands.put("quit", QuitWork::new);
    knownCommands.put("remove", () ->
        new RemoveWork(
            getNextInput("Enter work name:", true)
        ));
    knownCommands.put("rename", () ->
        new RenameWork(
            getNextInput("Enter work name:", true),
            getNextInput("Enter new name:", true)
        ));
    knownCommands.put("start", () -> new StartWork(
        getNextInput("Enter new work name:", true)
    ));
    // Work commands
    knownCommands.put("setName", () ->
        new SetStoryName(
            getNextInput("Enter new name:", true)
        ));
    knownCommands.put("addStatus", () ->
        new AddStatusToWork(
            getNextInput("Enter status name:", true),
            getNextValidInput(true, "Enter initial value:", Utils::isStringNumber)
        ));
    knownCommands.put("removeStatus", () ->
        new RemoveStatus(
            getNextInput("Enter status name:", true)
        ));
    knownCommands.put("addChoice", AddChoice::new);
    knownCommands.put("setInitial", () ->
        new SetInitialChoice(
            getNextValidInput(true, "Enter choice number:", Utils::isPositiveStringNumber)
        ));
    knownCommands.put("addSimple", () ->
        new AddSimpleDecision(
            getNextInput("Enter description:", true),
            getNextValidInput(true, "Enter choice number:", Utils::isPositiveStringNumber),
            getNextValidInput(true, "Enter outcome number:", Utils::isPositiveStringNumber)
        ));
    knownCommands.put("addConsequential", () ->
        new AddConsequentialDecision(
            getNextInput("Enter description:", true),
            getNextValidInput(true, "Enter choice number:", Utils::isPositiveStringNumber),
            getNextValidInput(true, "Enter outcome number:", Utils::isPositiveStringNumber),
            getNextInput("Enter status update consequences, separated by commas:", true)
        ));
    knownCommands.put("addSimpleDependent", () ->
        new AddSimpleDependentDecision(
            getNextInput("Enter description:", true),
            getNextValidInput(true, "Enter choice number:", Utils::isPositiveStringNumber),
            getNextInput("Enter status dependency name:", true),
            getNextValidInput(true, "Enter threshold value:", Utils::isStringNumber),
            getNextValidInput(true,
                "Enter the number of the outcome if the value is below the threshold:",
                Utils::isPositiveStringNumber),
            getNextValidInput(true,
                "Enter the number of the outcome if the value meets the threshold:",
                Utils::isPositiveStringNumber)
        ));
    knownCommands.put("addConsequentialDependent", () ->
        new AddConsequentialDependentDecision(
            getNextInput("Enter description:", true),
            getNextValidInput(true, "Enter choice number:", Utils::isPositiveStringNumber),
            getNextInput("Enter status dependency name:", true),
            getNextValidInput(true, "Enter threshold value:", Utils::isStringNumber),
            getNextValidInput(true,
                "Enter the number of the outcome if the value is below the threshold:",
                Utils::isPositiveStringNumber),
            getNextValidInput(true,
                "Enter the number of the outcome if the value meets the threshold:",
                Utils::isPositiveStringNumber),
            getNextInput("Enter status update consequences, separated by commas:", true)
        ));
    knownCommands.put("removeDecision", () ->
        new RemoveDecision(
            getNextValidInput(true, "Enter choice number:", Utils::isPositiveStringNumber),
            getNextValidInput(true, "Enter option number:", Utils::isPositiveStringNumber)
        ));
    knownCommands.put("removeChoice", () ->
        new RemoveChoice(
            getNextValidInput(true, "Enter choice number:", Utils::isPositiveStringNumber)
        ));
    // ------ IO Commands ------ //
    ioCommands.put("export", () ->
        new ExportWork(
            getNextInput("Enter path to export to:", true)
        ));
  }
}

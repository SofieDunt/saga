package controller;

import java.io.IOException;
import java.util.Scanner;
import java.util.function.Predicate;
import utils.IOUtils;
import utils.Utils;
import view.LibraryApplicationView;

/**
 * An abstract controller for a story player application to handle the logic of processing user
 * input.
 */
public abstract class AbstractController implements ApplicationController {

  // The view the controller controls
  private final LibraryApplicationView view;
  // The scanner reading through user inputs
  private final Scanner scanner;

  // The constant representing a command to quit, should be considered case-insensitive
  private static final String QUIT_COMMAND = "q";

  /**
   * Constructs an {@code AbstractController} that controls the given view and reads input from the
   * given readable.
   *
   * @param view     the view
   * @param readable the readable to read from
   * @throws IllegalArgumentException if any argument is null
   */
  protected AbstractController(LibraryApplicationView view, Readable readable)
      throws IllegalArgumentException {
    Utils.ensureNotNull(view, "View can't be null");
    Utils.ensureNotNull(readable, "Readable can't be null");

    this.view = view;
    this.scanner = new Scanner(readable);
  }

  @Override
  public void play() throws IllegalStateException {
    welcome();
    boolean hasQuit = false;

    // Until the user quits, handle input
    while (!hasQuit) {
      hasQuit = handleUserInputs();
    }

    // Inform the user the application has been quit
    tryRenderMessage("Application closed.");
  }

  /**
   * Checks if the given string is a quit command.
   *
   * @param str the string
   * @return true if it is a quit command, false if otherwise
   */
  protected boolean isQuitCommand(String str) {
    return str.compareToIgnoreCase(QUIT_COMMAND) == 0;
  }

  /**
   * Handles user inputs for the user to enter commands. If reading input fails, quits.
   *
   * @return true if the user has quit, false if otherwise
   * @throws IllegalStateException if writing to the Appendable object used by the controller
   */
  private boolean handleUserInputs() throws IllegalStateException {
    defaultRender();

    // Get the next command identifier
    String baseCommand = getNextValidInput(false, "Enter a valid command:",
        this::isBaseCommand);

    // If the next command is to quit, quit
    if (isQuitCommand(baseCommand)) {
      return true;
    }

    // Execute the next command.
    try {
      executeBaseCommand(baseCommand);
    } catch (RuntimeException e) {
      // If the command fails (model throws an exception), display an informative error message.
      // There are two possible exceptions: IllegalArgumentException and IllegalStateException
      tryRenderMessage("Could not " + baseCommand + ": " + e.getMessage() + "\n");
    } catch (IOException e) {
      // If the command fails due to an IO exception, display an informative error
      tryRenderMessage("Could not " + baseCommand + ": " + "IO failure: " + e.getMessage() + "\n");
    }

    // The user has not quit
    return false;
  }

  /**
   * Checks if the given string is a base command.
   *
   * @param s the string
   * @return true if the string is a base command, false if otherwise
   */
  protected boolean isBaseCommand(String s) {
    return isQuitCommand(s);
  }

  /**
   * Executes the base command represented by the given string. Assumes the given string is a valid
   * base command.
   *
   * @param baseCommand the base command
   * @throws IOException              if the model throws an IOException while executing the
   *                                  command
   * @throws IllegalArgumentException if the model throws an IllegalArgumentException while
   *                                  executing the command
   */
  protected abstract void executeBaseCommand(String baseCommand)
      throws IOException, IllegalArgumentException;

  /**
   * Asks the user for input with the given message and gets the next input.
   *
   * @param creatingCommand tells whether or not the input is being used for a command object
   * @param message         the message to render asking for input
   * @return the next input
   */
  protected String getNextInput(String message, boolean creatingCommand) {
    // Render message asking for input
    tryRenderMessage(message);
    tryRenderMessage("\n");
    // Return next input
    return tryAdvanceNext(creatingCommand);
  }

  /**
   * Asks the user for input with the given message and gets the next valid input from the readable,
   * where input is valid if it passes the given predicate. Prompts the user to enter valid input
   * after invalid inputs.
   *
   * @param creatingCommand  tells whether or not the input is being used for a command object
   * @param message          the message to render asking for the input
   * @param isValidInputPred the predicate that returns true if input is valid
   * @return the next valid command as a String
   * @throws IllegalArgumentException if any argument is null
   * @throws IllegalStateException    if appending to the Appendable fails
   */
  protected String getNextValidInput(boolean creatingCommand, String message,
      Predicate<String> isValidInputPred)
      throws IllegalArgumentException, IllegalStateException {
    Utils.ensureNotNull(message, "Message can't be null");
    Utils.ensureNotNull(isValidInputPred, "Predicate can't be null");

    // Get the next input
    String nextInput = getNextInput(message, creatingCommand);

    // While the input is invalid, keep asking for and getting next input
    while (!isValidInputPred.test(nextInput)) {
      if (creatingCommand && nextInput == null) { // if input is for command object, can pass
        return null;
      }
      nextInput = getNextInput("Bad input. " + message, creatingCommand);
    }

    // Return the valid input
    return nextInput;
  }

  /**
   * Advances the given scanner to the next token, if possible, returning the passed token.
   * Otherwise returns a quit command or null depending on whether or not the token is for a command
   * object. If the string begins with {@code "}, returns all content, including whitespace and with
   * newlines as whitespace, between the first character and the next {@code "}.
   *
   * @param creatingCommand tells whether or not the input is being used for a command object
   * @return the next token of the scanner or a quit command/null if there is no next
   */
  private String tryAdvanceNext(boolean creatingCommand) {
    try {
      return IOUtils.tryNext(this.scanner, "");
    } catch (IllegalArgumentException e) {
      if (!creatingCommand) {
        return QUIT_COMMAND;
      } else {
        return null;
      }
    }
  }

  /**
   * Commands the view to render the given message.
   *
   * @param message the message to render
   * @throws IllegalStateException if the message can't be rendered
   */
  protected void tryRenderMessage(String message) throws IllegalStateException {
    try {
      this.view.renderMessage(message);
    } catch (IOException e) {
      throw new IllegalStateException("The view can not be rendered.");
    }
  }

  /**
   * Renders something to be shown before processing each user input.
   */
  protected abstract void defaultRender() throws IllegalStateException;

  /**
   * Renders something when the application first starts.
   */
  protected abstract void welcome() throws IllegalStateException;

  /**
   * Commands the view to render the current choice.
   *
   * @throws IllegalStateException if the choice can't be rendered
   */
  protected void tryRenderCurrent() throws IllegalStateException {
    try {
      this.view.renderCurrent();
      tryRenderMessage("\n");
    } catch (IOException e) {
      throw new IllegalStateException("The view can not be rendered.");
    }
  }

  /**
   * Commands the view to render the story library.
   *
   * @throws IllegalStateException if the library can't be rendered
   */
  protected void tryRenderLibrary() throws IllegalStateException {
    try {
      this.view.renderLibrary();
      tryRenderMessage("\n");
    } catch (IOException e) {
      throw new IllegalStateException("The view can not be rendered.");
    }
  }
}

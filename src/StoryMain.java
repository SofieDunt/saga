import controller.PlayerController;
import controller.WriterController;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import model.SimpleStoryPlayerModel;
import model.SimpleStoryWriterModel;

/**
 * A main class to use the story writer or player application.
 */
public class StoryMain {

  /**
   * Main method to accept command line inputs and run the story writer application.
   *
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    if (args.length < 2) {
      printSupportedCommands();
      return;
    }

    switch (args[0]) {
      case "-play":
        runPlayer(args);
        break;
      case "-write":
        runWriter(args);
        break;
      default:
        printSupportedCommands();
    }
  }

  /**
   * Executes the player controller according to the input.
   *
   * @param args the command line arguments
   */
  private static void runPlayer(String[] args) {
    if (args[1].equals("-script") && args.length > 2) {
      Readable script;
      try {
        script = new FileReader(args[2]);
      } catch (IOException e) {
        System.out.println("Script could not be read, invalid file path");
        return;
      }
      new PlayerController(new SimpleStoryPlayerModel(), script, System.out).play();
    } else if (args[1].equals("-interactive")) {
      new PlayerController(new SimpleStoryPlayerModel(), new InputStreamReader(System.in),
          System.out).play();
    } else {
      printSupportedCommands();
    }
  }

  /**
   * Executes the writer controller according to the input.
   *
   * @param args the command line arguments
   */
  private static void runWriter(String[] args) {
    if (args[1].equals("-script") && args.length > 2) {
      Readable script;
      try {
        script = new FileReader(args[2]);
      } catch (IOException e) {
        System.out.println("Script could not be read, invalid file path");
        return;
      }
      new WriterController(new SimpleStoryWriterModel(), script, System.out).play();
    } else if (args[1].equals("-interactive")) {
      new WriterController(new SimpleStoryWriterModel(), new InputStreamReader(System.in),
          System.out).play();
    } else {
      printSupportedCommands();
    }
  }

  /**
   * Prints supported commands to the console.
   */
  private static void printSupportedCommands() {
    System.out.println("Invalid command input. Supported commands are:");
    System.out.println(
        "to play stories by executing a script at a known filePath`: -play -script filePath");
    System.out.println(
        "to play stories interactively via the console: -play -interactive");
    System.out.println(
        "to write stories by executing a script at a known filePath`: -write -script filePath");
    System.out.println(
        "to write stories interactively via the console: -write -interactive");
  }
}

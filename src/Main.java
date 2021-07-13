import controller.PlayerController;
import controller.StoryPlayerController;
import java.io.InputStreamReader;
import model.SimpleStoryPlayerModel;
import model.StoryPlayerModel;
import model.game.StoryGame;

/**
 * A main class to use the story player application through the console.
 */
public class Main {

  /**
   * Starts a controller that takes input from and outputs to the console.
   *
   * @param args the command line parameters used when running the program
   */
  public static void main(String[] args) {
    StoryPlayerModel<StoryGame> model = new SimpleStoryPlayerModel();
    StoryPlayerController controller = new PlayerController(model, new InputStreamReader(System.in),
        System.out);
    controller.play();
  }
}

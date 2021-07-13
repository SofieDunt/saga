package controller.command;

import controller.command.IOCommand;
import io.TextImporter;
import model.StoryPlayerModel;
import model.game.StoryGame;
import utils.Utils;

/**
 * A command object to import a story from a text file and add it to the model's library.
 */
public class ImportStory implements IOCommand<StoryPlayerModel<StoryGame>> {

  private final String filePath;

  /**
   * Constructs the command object to import the story from the given path and add it to the model.
   *
   * @param filePath the file path of the story to import
   */
  public ImportStory(String filePath) {
    this.filePath = filePath;
  }

  @Override
  public void execute(StoryPlayerModel<StoryGame> model)
      throws IllegalArgumentException {
    Utils.ensureNotNull(model, "Model can't be null");
    model.addStory(new TextImporter().importStory(this.filePath));
  }
}

package controller.command;

import io.TextImporter;
import model.StoryWriterModel;
import model.game.StoryGame;
import utils.Utils;

/**
 * A command object to import a story from a text file to the writer model's library;
 */
public class ImportWork implements IOCommand<StoryWriterModel<StoryGame>> {

  private final String filePath;

  /**
   * Constructs the command object to import the story from the given path and add it to the writer
   * model.
   *
   * @param filePath the file path of the story to import
   */
  public ImportWork(String filePath) {
    this.filePath = filePath;
  }

  @Override
  public void execute(StoryWriterModel<StoryGame> model)
      throws IllegalArgumentException {
    Utils.ensureNotNull(model, "Model can't be null");
    model.add(new TextImporter().importStory(this.filePath));
  }
}

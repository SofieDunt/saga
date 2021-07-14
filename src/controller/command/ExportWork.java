package controller.command;

import io.TextExporter;
import java.io.IOException;
import model.StoryWriterModel;
import model.game.StoryGame;
import utils.Utils;

/**
 * A command object to create and export the current work of the writer model to a specified path.
 */
public class ExportWork implements IOCommand<StoryWriterModel<StoryGame>> {

  private final String filePath;

  /**
   * Constructs the command object to create and export the work to the given path.
   *
   * @param filePath the path to export to
   */
  public ExportWork(String filePath) {
    this.filePath = filePath;
  }

  @Override
  public void execute(StoryWriterModel<StoryGame> model)
      throws IllegalArgumentException, IOException {
    Utils.ensureNotNull(model, "Model can't be null");
    new TextExporter().export(model.create(), this.filePath);
  }
}

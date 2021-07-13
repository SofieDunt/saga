package controller.command;

import controller.command.IOCommand;
import io.TextExporter;
import java.io.IOException;
import model.StoryPlayerModel;
import model.game.StoryGame;
import utils.Utils;

/**
 * A command object to export a story as a text file, either the in-progress or original version,
 * from the model's library.
 */
public class ExportStory implements IOCommand<StoryPlayerModel<StoryGame>> {

  private final String filePath;
  private final String storyName;
  private final boolean original;

  /**
   * Constructs the command object to export the story of the given name from the model to the given
   * file path. Note the file path and story name are allowed to be null (any errors will be caught
   * by the model/importer).
   *
   * @param filePath  the file path to export the story to
   * @param storyName the name of the story to export
   * @param original  true to export the original version, false to export the in-progress version
   */
  public ExportStory(String filePath, String storyName, boolean original) {
    this.filePath = filePath;
    this.storyName = storyName;
    this.original = original;

  }

  @Override
  public void execute(StoryPlayerModel<StoryGame> model)
      throws IllegalArgumentException, IOException {
    Utils.ensureNotNull(model, "Model can't be null");
    StoryGame toExport = model.getStory(this.storyName);
    if (this.original) {
      toExport = toExport.getOriginalStory();
    }
    new TextExporter().export(toExport, this.filePath);
  }
}

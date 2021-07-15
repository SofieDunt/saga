package io;

import java.io.File;
import java.io.IOException;
import model.game.StoryGame;

/**
 * An interface for function objects that export {@link StoryGame}s to specified paths in some
 * format.
 */
public interface StoryExporter {

  /**
   * Exports the given story to a file at the given file path.
   *
   * @param story    the story to export
   * @param filePath the path to export the file to
   * @return the resulting file
   * @throws IllegalArgumentException if the given story can't be exported (ex. is null)
   * @throws IOException              if the file can not be written
   */
  File export(StoryGame story, String filePath) throws IllegalArgumentException, IOException;
}

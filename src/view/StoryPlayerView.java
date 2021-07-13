package view;

import java.io.IOException;

/**
 * An interface for a view for a story player application that can display the current story library
 * and the story being played.
 */
public interface StoryPlayerView {

  /**
   * Render the current choice of the story being played to the provided data destination.
   *
   * @throws IOException if transmission to the data destination fails
   */
  void renderCurrent() throws IOException;

  /**
   * Render the user's story library to the provided data destination.
   *
   * @throws IOException if transmission to the data destination fails
   */
  void renderLibrary() throws IOException;

  /**
   * Render a specific message to the provided data destination.
   *
   * @param message the message to render
   * @throws IOException if transmission to the data destination fails
   */
  void renderMessage(String message) throws IOException;
}

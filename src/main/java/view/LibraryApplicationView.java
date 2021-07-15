package view;

import java.io.IOException;

/**
 * An interface for a view for an application that manages some library and processes one record of
 * the library at a time.
 */
public interface LibraryApplicationView {

  /**
   * Render the current record to the data destination.
   *
   * @throws IOException if transmission to the data destination fails
   */
  void renderCurrent() throws IOException;

  /**
   * Render the library to the data destination.
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

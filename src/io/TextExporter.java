package io;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import model.game.Choice;
import model.game.StoryGame;
import model.game.decision.Decision;
import utils.IOUtils;
import utils.Utils;

/**
 * A {@link StoryExporter} that exports stories as text files that can be read by {@link
 * TextImporter}s.
 */
public class TextExporter implements StoryExporter {

  @Override
  public File export(StoryGame story, String filePath)
      throws IllegalArgumentException, IOException {
    Utils.ensureNotNull(story, "Story can't be null.");
    filePath = getFilePath(story, filePath);

    // Set up to write to file
    File storyFile = new File(filePath);
    FileWriter writer;
    try {
      writer = new FileWriter(storyFile);
    } catch (IOException e) {
      throw new IllegalArgumentException("Can't write to path: " + filePath);
    }

    // Write story title
    String storyName = story.getName();
    appendInQuotes(writer, storyName);
    writer.append("\n");

    // Write statuses
    Map<String, Integer> statuses = story.getStatuses();
    writer.append("[ ");
    for (Entry<String, Integer> status : statuses.entrySet()) {
      appendInQuotes(writer, status.getKey());
      writer.append(" ").append(status.getValue().toString()).append(" ");
    }
    writer.append("]\n");

    // Get story information
    Choice firstChoice = story.getCurrentChoice();
    StoryNodes nodes = StoryNodes.createNodes(story);
    List<Choice> choices = nodes.getChoices();
    Map<Choice, String> choiceOptions = nodes.getChoiceOptions();
    Map<Choice, String> choiceIds = new HashMap<>();
    for (Choice choice : choiceOptions.keySet()) {
      choiceIds.put(choice, choiceOptions.get(choice).substring(0, 2));
    }
    List<Decision> storyDecisions = nodes.getDecisions();

    // Write number of choices
    writer.append(Integer.toString(choices.size())).append("\n");

    // Write decisions
    writer.append("{\n");
    for (Decision decision : storyDecisions) {
      writer
          .append("[ ")
          .append(decision.export(choiceIds))
          .append(" ]\n");
    }
    writer.append("}\n");

    // Write choices
    writer.append("{\n");
    for (Choice choice : choices) {
      writer.append(choiceOptions.get(choice)).append("\n");
    }
    writer.append("}\n");
    // Write first choice number
    writer.append("C").append(Integer.toString(choices.indexOf(firstChoice)));

    // Close and return
    writer.close();
    return storyFile;
  }

  /**
   * Gets the file path to write the story to. Assumes the given story is not null.
   *
   * @param story    the story
   * @param filePath the preferred story path
   * @return the file path
   */
  private static String getFilePath(StoryGame story, String filePath) {
    if (filePath != null) {
      return filePath;
    } else {
      return "." + IOUtils.pathSeparator() + story.getName() + ".txt";
    }
  }

  /**
   * Appends the given string to the given file writer surrounded by quotation marks. Assumes
   * neither argument is null.
   *
   * @param writer the file writer to append to
   * @param string the string to append
   * @throws IOException if writing to the file fails
   */
  private static void appendInQuotes(FileWriter writer, String string) throws IOException {
    writer.append("\"").append(string).append("\"");
  }
}

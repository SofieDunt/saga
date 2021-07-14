package io;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;
import model.game.Choice;
import model.game.SimpleChoice;
import model.game.SimpleStoryGame;
import model.game.StoryGame;
import model.game.decision.Decision;
import model.game.decision.DecisionCreator;
import utils.IOUtils;
import utils.Utils;

/**
 * A {@link StoryImporter} that imports text files as stories, as exported by {@link
 * TextExporter}s.
 */
public class TextImporter implements StoryImporter {

  private static final String ILLEGAL_FORMAT_MESSAGE = "Illegal format - not a valid story file";

  @Override
  public StoryGame importStory(String filePath) throws IllegalArgumentException {
    Utils.ensureNotNull(filePath, "Filename can't be null");

    Scanner sc;
    try {
      sc = new Scanner(new FileInputStream(filePath));
    } catch (IOException e) {
      throw new IllegalArgumentException("File not found");
    }

    // Read story name
    String storyName = tryNext(sc);

    // Read statuses
    Map<String, Integer> statuses = readStatuses(sc);

    // Initialize choices
    List<Choice> choices = new ArrayList<>();
    List<List<Decision>> choiceDecisions = new ArrayList<>();
    int numChoices;
    try {
      numChoices = sc.nextInt();
    } catch (NoSuchElementException e) {
      throw new IllegalArgumentException(ILLEGAL_FORMAT_MESSAGE);
    }
    for (int i = 0; i < numChoices; i++) {
      choiceDecisions.add(new ArrayList<>());
      choices.add(new SimpleChoice(choiceDecisions.get(i)));
    }

    // Read all decisions
    List<Decision> decisions = readDecisions(sc, choices);

    // Read choices, add options
    addOptions(sc, decisions, choiceDecisions);

    return new SimpleStoryGame(storyName, choices.get(getNumId(tryNext(sc))), statuses);
  }

  /**
   * Gets the next in the scanner, throwing an exception if there is none.
   *
   * @param sc the scanner
   * @return the next in the scanner
   * @throws IllegalArgumentException if there is no next
   */
  private static String tryNext(Scanner sc) throws IllegalArgumentException {
    return IOUtils.tryNext(sc, ILLEGAL_FORMAT_MESSAGE);
  }

  /**
   * Reads the statuses from the file, returning the map of statuses.
   *
   * @param sc the scanner
   * @return the map of statuses described in the file
   * @throws IllegalArgumentException if the file is of an illegal format
   */
  private static Map<String, Integer> readStatuses(Scanner sc)
      throws IllegalArgumentException {
    Map<String, Integer> statuses = new HashMap<>();
    tryNext(sc); // [
    String next = tryNext(sc);
    while (!next.equals("]")) {
      statuses.put(next, IOUtils.tryNextInt(sc, ILLEGAL_FORMAT_MESSAGE));
      next = tryNext(sc);
    }
    return statuses;
  }

  /**
   * Reads the decisions from the file, returning the list of decisions.
   *
   * @param sc      the scanner
   * @param choices the list of choices in the story
   * @return the list of decisions described in the file
   * @throws IllegalArgumentException if the file is of an illegal format
   */
  private static List<Decision> readDecisions(Scanner sc, List<Choice> choices)
      throws IllegalArgumentException {
    tryNext(sc); // {
    String next = tryNext(sc);
    List<Decision> decisions = new ArrayList<>();
    while (!next.equals("}")) {
      while (!next.equals("]")) {
        decisions.add(DecisionCreator.importDecision(sc, choices));
        next = tryNext(sc);
      }
      next = tryNext(sc);
    }
    return decisions;
  }

  /**
   * Gets the number from a choice/decision id, in the format C#/D#.
   *
   * @param stringId the choice/decision id
   * @return the number of the id
   * @throws IllegalArgumentException if the id is of an illegal format
   */
  private static int getNumId(String stringId) throws IllegalArgumentException {
    return IOUtils.getNumId(stringId, ILLEGAL_FORMAT_MESSAGE);
  }

  /**
   * Adds the next decision options described in the scanner to the given lists of given choice
   * options, adding each decision to its respective choice's list of options.
   *
   * @param sc              the scanner
   * @param decisions       the list of decisions
   * @param choiceDecisions the list of list of decisions, representing a list of choice options
   * @throws IllegalArgumentException if the options are of an illegal format
   */
  private static void addOptions(Scanner sc, List<Decision> decisions,
      List<List<Decision>> choiceDecisions) throws IllegalArgumentException {
    tryNext(sc); // {
    String next = tryNext(sc); // C#
    while (!next.equals("}")) {
      int choiceNum = getNumId(next);
      tryNext(sc); // [
      String nextDecision = tryNext(sc);
      while (!nextDecision.equals("]")) {
        choiceDecisions.get(choiceNum).add(decisions.get(getNumId(nextDecision)));
        nextDecision = tryNext(sc);
      }
      next = tryNext(sc); // C# or }
    }
  }
}

package model.game.decision;

import com.response.DecisionResponse;
import com.response.StatusUpdateResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import model.game.Choice;
import model.game.statusUpdate.StatusUpdate;
import model.game.statusUpdate.StatusUpdateCreator;
import utils.IOUtils;
import utils.Utils;

/**
 * A creator class for creating instances of {@link Decision}s.
 */
public class DecisionCreator {

  private static final String ILLEGAL_FORMAT_MESSAGE = "Illegal format - not a valid decision";
  private static final String ILLEGAL_DETERMINER_FORMAT_MESSAGE =
      "Illegal format - not a valid outcome determiner";

  /**
   * Creates a decision from the next information in the given scanner and a list of choices that
   * contains the decision's outcome(s).
   *
   * @param sc      the scanner
   * @param choices the list of choices, containing the decisions outcome(s) at the index (or
   *                indices) indicated by the scanner
   * @return the decision represented by the next in the scanner
   * @throws IllegalArgumentException if the next information in the scanner does not represent a
   *                                  decision or if any of the arguments are null
   */
  public static Decision importDecision(Scanner sc, List<Choice> choices)
      throws IllegalArgumentException {
    Utils.ensureNotNull(sc, "Scanner can't be null");
    Utils.ensureNotNull(choices, "Choices can't be null");

    DecisionTypes type = getType(sc);

    switch (type) {
      case SIMPLE:
        return new SimpleDecision(tryNext(sc), getChoice(sc, choices));
      case CONSEQUENTIAL:
        Map<String, StatusUpdate> consequences = new HashMap<>();
        String description = tryNext(sc);
        String next = tryNext(sc); // [
        while (!next.equals("]")) {
          StatusUpdate statusUpdate = StatusUpdateCreator.importSimple(sc);
          consequences.put(tryNext(sc), statusUpdate);
          next = tryNext(sc);
        }
        return new ConsequentialDecision(description, getChoice(sc, choices), consequences);
      case DEPENDENT:
        OutcomeDeterminer determiner = importDeterminer(sc, choices);
        tryNext(sc); // delegate [
        return new DependentDecision(importDecision(sc, choices), determiner);
      default:
        throw new IllegalArgumentException(ILLEGAL_FORMAT_MESSAGE);
    }
  }

  /**
   * Creates a decision response of the given id from the next information in the given scanner and
   * a list of choices that contains the decision's outcome(s).
   *
   * @param id      the id of the decision response
   * @param sc      the scanner
   * @param choices the list of choices, containing the decisions outcome(s) at the index (or
   *                indices) indicated by the scanner
   * @return the decision represented by the next in the scanner
   * @throws IllegalArgumentException if the next information in the scanner does not represent a
   *                                  decision or if any of the arguments are null
   */
  public static DecisionResponse createDecisionResponse(int id, Scanner sc, List<Choice> choices)
      throws IllegalArgumentException {
    Utils.ensureNotNull(sc, "Scanner can't be null");
    Utils.ensureNotNull(choices, "Choices can't be null");

    DecisionTypes type = getType(sc);
    DecisionResponse response = new DecisionResponse(id, type);

    switch (type) {
      case SIMPLE:
        response.setDescription(tryNext(sc));
        response.setOutcome1Id(IOUtils.getNumId(tryNext(sc), ILLEGAL_FORMAT_MESSAGE));
        return response;
      case CONSEQUENTIAL:
        List<StatusUpdateResponse> updateResponses = new ArrayList<>();
        response.setDescription(tryNext(sc));
        String next = tryNext(sc); // [
        while (!next.equals("]")) {
          updateResponses.add(StatusUpdateCreator.createResponse(sc));
          next = tryNext(sc);
        }
        response.setConsequences(updateResponses);
        response.setOutcome1Id(IOUtils.getNumId(tryNext(sc), ILLEGAL_FORMAT_MESSAGE));
        return response;
      case DEPENDENT:
        response = readDeterminer(response, sc);
        tryNext(sc); // delegate [
        DecisionResponse delegateResponse = createDecisionResponse(id, sc, choices);
        response.setDescription(delegateResponse.getDescription());
        response.setConsequences(delegateResponse.getConsequences());
        return response;
      default:
        throw new IllegalArgumentException(ILLEGAL_FORMAT_MESSAGE);
    }
  }

  /**
   * Creates an outcome determiner from the next information in the given scanner and a list of
   * choices that contains the possible outcomes.
   *
   * @param sc      the scanner
   * @param choices the list of choices, containing the possible outcomes at the indices indicated
   *                by the scanner
   * @return the outcome determiner represented by the next in the scanner
   * @throws IllegalArgumentException if the next information in the scanner does not represent an
   *                                  outcome determiner or any of the given arguments are null
   */
  public static OutcomeDeterminer importDeterminer(Scanner sc, List<Choice> choices)
      throws IllegalArgumentException {
    Utils.ensureNotNull(sc, "Scanner can't be null");
    Utils.ensureNotNull(choices, "Choices can't be null");

    String type = tryNextDeterminer(sc);

    switch (type) {
      case "TWOTHRESHOLD":
        return new TwoThresholdDeterminer(
            tryNextDeterminer(sc),
            IOUtils.tryNextInt(sc, ILLEGAL_DETERMINER_FORMAT_MESSAGE),
            getChoice(sc, choices),
            getChoice(sc, choices));
      default:
        throw new IllegalArgumentException(ILLEGAL_DETERMINER_FORMAT_MESSAGE);
    }
  }

  /**
   * Updates the given decision response with the information about the determiner represented by
   * the next scanner values.
   *
   * @param response the decision response
   * @param sc       the scanner
   * @return the updated decision response
   * @throws IllegalArgumentException if any argument is null, the scanner does not have a next, or
   *                                  the next values in the scanner do not represent a determiner
   */
  public static DecisionResponse readDeterminer(DecisionResponse response, Scanner sc) {
    Utils.ensureNotNull(response, "Response can't be null");
    Utils.ensureNotNull(sc, "Scanner can't be null");

    String type = tryNextDeterminer(sc);

    switch (type) {
      case "TWOTHRESHOLD":
        response.setDependency(tryNextDeterminer(sc));
        response.setThreshold(IOUtils.tryNextInt(sc, ILLEGAL_DETERMINER_FORMAT_MESSAGE));
        response.setOutcome1Id(
            IOUtils.getNumId(tryNextDeterminer(sc),
                ILLEGAL_DETERMINER_FORMAT_MESSAGE));
        response.setOutcome2Id(
            IOUtils.getNumId(tryNextDeterminer(sc), ILLEGAL_DETERMINER_FORMAT_MESSAGE));
      default:
        throw new IllegalArgumentException(ILLEGAL_DETERMINER_FORMAT_MESSAGE);
    }
  }

  /**
   * Gets the decision type represented by the next of the scanner.
   *
   * @param sc the scanner
   * @return the decision type
   * @throws IllegalArgumentException if the scanner does not have a next or the next does not
   *                                  represent a decision type
   */
  private static DecisionTypes getType(Scanner sc) throws IllegalArgumentException {
    String stringType = tryNext(sc);
    try {
      return DecisionTypes.valueOf(stringType);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException(ILLEGAL_FORMAT_MESSAGE);
    }
  }

  /**
   * Gets the next in the scanner, throwing an exception indicating an illegal decision format if
   * there is none.
   *
   * @param sc the scanner
   * @return the next in the scanner
   * @throws IllegalArgumentException if there is no next
   */
  private static String tryNext(Scanner sc) throws IllegalArgumentException {
    return IOUtils.tryNext(sc, ILLEGAL_FORMAT_MESSAGE);
  }

  /**
   * Gets the next in the scanner, throwing an exception indicating an illegal determiner format if
   * there is none.
   *
   * @param sc the scanner
   * @return the next in the scanner
   * @throws IllegalArgumentException if there is no next
   */
  private static String tryNextDeterminer(Scanner sc) throws IllegalArgumentException {
    return IOUtils.tryNext(sc, ILLEGAL_DETERMINER_FORMAT_MESSAGE);
  }

  /**
   * Gets the next choice described by the scanner from the given list of choices.
   *
   * @param sc      the scanner
   * @param choices the list of choices in the story
   * @return the choice described by the scanner
   * @throws IllegalArgumentException if the file is of an illegal format or if any of the arguments
   *                                  are null
   */
  private static Choice getChoice(Scanner sc, List<Choice> choices)
      throws IllegalArgumentException {
    String illegalFormatMessage = "Illegal format - not a valid choice";
    String next = IOUtils.tryNext(sc, illegalFormatMessage);
    if (next.charAt(0) == 'C') {
      int choiceIdx = IOUtils.getNumId(next, illegalFormatMessage);
      if (choiceIdx < choices.size()) {
        return choices.get(choiceIdx);
      }
    }
    throw new IllegalArgumentException(illegalFormatMessage);
  }
}

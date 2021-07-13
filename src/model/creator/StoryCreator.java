package model.creator;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * An interface for a model of a story creator application that allows users to easily create a
 * single story.
 *
 * @param <K> the type of the stories the creator creates
 */
public interface StoryCreator<K> extends StoryCreatorState {

  /**
   * Creates the story.
   *
   * @return the stor
   */
  K create();

  /**
   * Exports the story as a text file to the given path.
   *
   * @param filePath the path to export to
   * @return the story
   * @throws IllegalArgumentException if the path is invalid
   * @throws IOException              if exporting fails due to an IO error
   */
  File export(String filePath) throws IllegalArgumentException, IOException;


  /**
   * Sets the name of the story being created to the given name.
   *
   * @param name the name
   * @throws IllegalArgumentException if the given name is null
   */
  void setStoryName(String name) throws IllegalArgumentException;

  /**
   * Adds the given status to the story. If the status already exists, updates it to the new value.
   *
   * @param name the name of the status
   * @param val  the initial value of the status
   * @throws IllegalArgumentException if the given name is null
   */
  void addStatus(String name, int val) throws IllegalArgumentException;

  /**
   * Removes the given status from the story.
   *
   * @param name the name of the status
   * @throws IllegalArgumentException if the name is null, there is no status of the given name, a
   *                                  consequential decision updates the status, or a dependent
   *                                  decision is dependent on the status
   */
  void removeStatus(String name) throws IllegalArgumentException;

  /**
   * Adds a choice to the story.
   *
   * @return the index of the choice
   */
  int addChoice();

  /**
   * Sets the initial choice of the story to the choice at the given index.
   *
   * @param idx the index of the choice, starting at 0
   * @throws IllegalArgumentException if the given index is invalid
   */
  void setFirstChoice(int idx) throws IllegalArgumentException;

  /**
   * Adds a simple decision as an option to the given choice.
   *
   * @param description the decision description
   * @param choiceIdx   the index of the choice to add the option to, starting at 0
   * @param outcomeIdx  the index of the outcome of the decision, starting at 0
   * @throws IllegalArgumentException if either given index is invalid or the description is null
   */
  void addSimpleDecision(String description, int choiceIdx, int outcomeIdx)
      throws IllegalArgumentException;

  /**
   * Adds a decision with the given consequences as an option to the given choice. Ignores nulls in
   * the list of status updates. If the list is empty, adds a simple decision.
   *
   * @param description  the decision description
   * @param choiceIdx    the index of the choice to add the option to, starting at 0
   * @param outcomeIdx   the index of the outcome of the decision, starting at 0
   * @param consequences a list of the status update consequences, represented as they are exported
   * @throws IllegalArgumentException if either given index is invalid or the list of status updates
   *                                  references a status that is not in the story or the
   *                                  description is null or a status update is invalid or the list
   *                                  is null
   */
  void addConsequentialDecision(String description, int choiceIdx, int outcomeIdx,
      List<String> consequences) throws IllegalArgumentException;


  /**
   * Adds a simple dependent decision as an option to the given choice.
   *
   * @param description     the decision description
   * @param choiceIdx       the index of the choice to add the option to, starting at 0
   * @param dependency      the status the decision is dependent on
   * @param threshold       the threshold of the status
   * @param outcomeBelowIdx the index of the outcome of the decision if the dependency is below the
   *                        threshold, starting at 0
   * @param outcomeMeetsIdx the index of the outcome of the decision if the dependency meets the
   *                        threshold, starting at 0
   * @throws IllegalArgumentException if any of the given indices is invalid or any given string is
   *                                  null
   */
  void addSimpleDependentThresholdDecision(String description, int choiceIdx, String dependency,
      int threshold, int outcomeBelowIdx, int outcomeMeetsIdx) throws IllegalArgumentException;


  /**
   * Adds a dependent decision with the given consequences as an option to the given choice. Ignores
   * nulls in the list of status updates. If the list is empty, adds a simple decision.
   *
   * @param description     the decision description
   * @param choiceIdx       the index of the choice to add the option to, starting at 0
   * @param dependency      the status the decision is dependent on
   * @param threshold       the threshold of the status
   * @param outcomeBelowIdx the index of the outcome of the decision if the dependency is below the
   *                        threshold, starting at 0
   * @param outcomeMeetsIdx the index of the outcome of the decision if the dependency meets the
   *                        threshold, starting at 0
   * @param consequences    a list of the status update consequences, represented as they are
   *                        exported
   * @throws IllegalArgumentException if any of the given indices is invalid or any given string is
   *                                  null or the list of status updates  references a status that
   *                                  is not in the story or the description is null or a status
   *                                  update is invalid or the list is null
   */
  void addConsequentialThresholdDecision(String description, int choiceIdx, String dependency,
      int threshold, int outcomeBelowIdx, int outcomeMeetsIdx, List<String> consequences)
      throws IllegalArgumentException;

  /**
   * Removes the given decision from the options of the given choice, also removing it from the
   * creator's decisions.
   *
   * @param choiceIdx   the index of the choice to add the option to, starting at 0
   * @param decisionIdx the index of the decision to remove, starting at 0
   * @throws IllegalArgumentException if either given index is invalid
   */
  void removeDecision(int choiceIdx, int decisionIdx) throws IllegalArgumentException;

  /**
   * Removes the given choice from the story.
   *
   * @param choiceIdx the index of the choice to remove
   * @throws IllegalArgumentException if the given index is invalid, any decision in the story uses
   *                                  the choice as an outcome, or the choice is the first choice in
   *                                  the story
   */
  void removeChoice(int choiceIdx) throws IllegalArgumentException;
}

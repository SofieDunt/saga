package controller;

import controller.service.request.AddConsequentialDecisionRequest;
import controller.service.request.AddConsequentialDependentRequest;
import controller.service.request.AddSimpleDecisionRequest;
import controller.service.request.AddSimpleDependentRequest;
import controller.service.response.ChoiceResponse;
import controller.service.response.DecisionResponse;
import controller.service.response.StoryResponse;
import controller.service.response.StoryStatusResponse;
import java.io.IOException;
import java.util.List;


/**
 * An interface for a controller of an application that allows users to play and create interactive
 * stories, to be used with a user interface through which users can make requests and obtain
 * information about the model state.
 */
public interface StoryApplicationController {

  // Player

  /**
   * Gets the story currently being played.
   *
   * @return the current story, as a story response.
   */
  StoryResponse getCurrentStory();


  /**
   * Gets the name of the current story being played, or null if there is no current story.
   *
   * @return the name of the current story
   */
  String getCurrentStoryName();

  /**
   * Gets the string representation of the current choice (next choice to be made) in the story
   * being played, or null if there is no current story.
   *
   * @return the current choice
   */
  String getCurrentChoice();

  /**
   * Gets the name of all stories in the user's player library.
   *
   * @return a list of all story names in the library
   */
  List<String> getAllStoryNames();

  /**
   * Exports the un-started version of the named story to a file at the given path.
   *
   * @param path the path to export to
   * @param name the name of the story to export
   * @throws IllegalArgumentException if the given path can't be exported to
   * @throws IOException              if an IO process fails during export
   */
  void exportStory(String path, String name) throws IllegalArgumentException, IOException;

  /**
   * Exports the named story as it is (in progress) to a file at the given path.
   *
   * @param path the path to export to
   * @param name the name of the story to export
   * @throws IllegalArgumentException if the given path can't be exported to
   * @throws IOException              if an IO process fails during export
   */
  void exportStoryInProgress(String path, String name) throws IllegalArgumentException, IOException;

  /**
   * Imports the story at the given path into the player.
   *
   * @param path the path of the story file
   * @throws IllegalArgumentException if there is no file at the path or the file is not a valid
   *                                  story file
   */
  void importStory(String path) throws IllegalArgumentException;

  /**
   * Makes the given decision in the loaded story.
   *
   * @param decision the index/id of the decision to be made, starting at 0
   * @throws IllegalArgumentException if the given decision does not exist
   * @throws IllegalStateException    if no story is loaded
   */
  void choose(int decision) throws IllegalArgumentException, IllegalStateException;

  /**
   * Loads the named story to be played.
   *
   * @param name the name of the story to be played
   * @throws IllegalArgumentException if no story of the given name exists in the user's player
   *                                  library
   */
  void loadStory(String name) throws IllegalArgumentException;

  /**
   * Restarts the current story.
   *
   * @throws IllegalStateException if no story is loaded
   */
  void restartStory() throws IllegalStateException;

  /**
   * Quits the current story.
   */
  void quitStory();

  /**
   * Removes the named story from the player library.
   *
   * @param name the name of the story
   * @throws IllegalArgumentException if no story of the given name exists
   */
  void removeStory(String name) throws IllegalArgumentException;

  // Writer

  /**
   * Gets a list of all works in the user's writer library.
   *
   * @return a list of all names
   */
  List<String> getAllWorkNames();

  /**
   * Gets the current work being edited as a story response, or null if no work is loaded.
   *
   * @return the current work
   */
  StoryResponse getCurrentWork();

  /**
   * Gets the name of the work currently being edited, or null if no work is loaded.
   *
   * @return the work name
   */
  String getCurrentWorkName();

  /**
   * Gets the name of the story of the work currently being edited, or null if no work is loaded.
   *
   * @return the name of the work's story
   */
  String getCurrentWorkStoryName();

  /**
   * Gets all statuses of the work currently being edited, or null if no work is loaded.
   *
   * @return a list of statuses as story status responses
   */
  List<StoryStatusResponse> getCurrentWorkStatuses();

  /**
   * Gets the id of the initial choice of the work currently being edited, or -1 if the loaded work
   * has no initial choice, or -123456789 if no work is loaded.
   *
   * @return the index id of the initial choice
   */
  int getCurrentWorkInitialChoice();

  /**
   * Gets all choices of the work currently being edited, or null if no work is loaded.
   *
   * @return a list of choices as choice responses
   */
  List<ChoiceResponse> getCurrentWorkChoices();

  /**
   * Gets all decisions of the work currently being edited, or null if no work is loaded.
   *
   * @return a list of decisions as decision responses
   */
  List<DecisionResponse> getCurrentWorkDecisions();

  /**
   * Exports the work currently being edited to the given path.
   *
   * @param path the path to export to
   * @throws IllegalArgumentException if the given path can't be written to
   * @throws IOException              if an IO failure occurs during export
   */
  void exportWork(String path) throws IllegalArgumentException, IOException;

  /**
   * Exports the work currently being edited to a playable story in the player.
   */
  void exportToPlayer();

  /**
   * Imports the story at the given path as a work to edit.
   *
   * @param path the path of the story to import
   * @throws IllegalArgumentException if no file exists at the path or the file is not a valid story
   *                                  file
   */
  void importToWriter(String path) throws IllegalArgumentException;

  /**
   * Loads the work of the given name to be edited.
   *
   * @param name the name of the work to load
   * @throws IllegalArgumentException if no work of the given name exists
   */
  void loadWork(String name) throws IllegalArgumentException;

  /**
   * Quits editing the current work.
   */
  void quitWork();

  /**
   * Removes the work of the given name from the writer library.
   *
   * @param name the name of the work
   * @throws IllegalArgumentException if no work of the given name exists
   */
  void removeWork(String name) throws IllegalArgumentException;

  /**
   * Renames the work in the library to the given new name.
   *
   * @param name    the current name of the work
   * @param newName the new name of the work
   * @throws IllegalArgumentException if no work of the given name exists or the new name is
   *                                  invalid
   */
  void renameWork(String name, String newName) throws IllegalArgumentException;

  /**
   * Creates a new work in the writer library of the given name.
   *
   * @param name the name of the new work to create
   */
  void startNewWork(String name);

  /**
   * Sets the name of the story of the current work to the given name.
   *
   * @param name the new name of the story
   * @throws IllegalArgumentException if the name is invalid
   * @throws IllegalStateException    if no work is loaded
   */
  void setStoryName(String name) throws IllegalArgumentException, IllegalStateException;

  /**
   * Adds the status of the given name to the story of the current work with the given initial
   * value. If the named status already exists, updates its initial value.
   *
   * @param name the name of the status
   * @param val  the initial value of the status
   * @throws IllegalArgumentException if the given name is invalid
   * @throws IllegalStateException    if no work is loaded
   */
  void addStatus(String name, int val) throws IllegalArgumentException, IllegalStateException;

  /**
   * Removes the status of the given name from the story of the current work.
   *
   * @param name the name of the status
   * @throws IllegalArgumentException if no status of the given name exists
   * @throws IllegalStateException    if no work is loaded
   */
  void removeStatus(String name) throws IllegalArgumentException, IllegalStateException;

  /**
   * Adds a choice in the story of the current work.
   *
   * @throws IllegalStateException if no work is loaded
   */
  void addChoice() throws IllegalStateException;

  /**
   * Sets the initial choice of the current work's story to be the given choice.
   *
   * @param choice the id/index of the choice
   * @throws IllegalArgumentException if the given choice does not exist
   * @throws IllegalStateException    if no work is loaded
   */
  void setInitialChoice(int choice) throws IllegalArgumentException, IllegalStateException;

  /**
   * Adds a simple decision to the current work's story as an option to an existing choice.
   *
   * @param request the request to add the decision
   * @throws IllegalArgumentException if the request is invalid
   * @throws IllegalStateException    if no work is loaded.
   */
  void addSimpleDecision(AddSimpleDecisionRequest request)
      throws IllegalArgumentException, IllegalStateException;

  /**
   * Adds a consequential decision to the current work's story as an option to an existing choice.
   *
   * @param request the request to add the decision
   * @throws IllegalArgumentException if the request is invalid
   * @throws IllegalStateException    if no work is loaded.
   */
  void addConsequentialDecision(AddConsequentialDecisionRequest request)
      throws IllegalArgumentException, IllegalStateException;

  /**
   * Adds a simple, dependent decision to the current work's story as an option to an existing
   * choice.
   *
   * @param request the request to add the decision
   * @throws IllegalArgumentException if the request is invalid
   * @throws IllegalStateException    if no work is loaded.
   */
  void addSimpleDependentDecision(AddSimpleDependentRequest request)
      throws IllegalArgumentException, IllegalStateException;

  /**
   * Adds a consequential, dependent decision to the current work's story as an option to an
   * existing choice.
   *
   * @param request the request to add the decision
   * @throws IllegalArgumentException if the request is invalid
   * @throws IllegalStateException    if no work is loaded.
   */
  void addConsequentialDependentDecision(AddConsequentialDependentRequest request)
      throws IllegalArgumentException, IllegalStateException;

  /**
   * Removes the given option from the given choice in the current work.
   *
   * @param choice the id/index of the choice
   * @param option the id/index of the option
   * @throws IllegalArgumentException if the choice or option does not exist
   * @throws IllegalStateException    if no work is loaded
   */
  void removeOption(int choice, int option) throws IllegalArgumentException, IllegalStateException;

  /**
   * Removes the given choice from the current work's story.
   *
   * @param choice the id/index of the choice
   * @throws IllegalArgumentException if the choice does not exist or can not be removed (is an
   *                                  outcome or the initial choice)
   * @throws IllegalStateException    if no work is loaded
   */
  void removeChoice(int choice) throws IllegalArgumentException, IllegalStateException;
}

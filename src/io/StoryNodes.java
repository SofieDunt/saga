package io;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import model.game.Choice;
import model.game.StoryGame;
import model.game.decision.Decision;
import utils.Utils;

/**
 * Represents all choices and decisions in a story.
 */
public class StoryNodes {

  private final List<Choice> choices;
  private final Map<Choice, String> choiceOptions;
  private final List<Decision> decisions;

  /**
   * Constructs a {@code StoryNodes} given a map of choices and their options, an order to put the
   * choices in, and all decisions in the story. Assumes all inputs are valid
   *
   * @param choiceOptionMap a map of choices to their list of options
   * @param choiceOrder     a list of all the choices in the desired order
   * @param decisions       a list of all decisions in the story
   */
  private StoryNodes(Map<Choice, List<String>> choiceOptionMap, List<Choice> choiceOrder,
      List<Decision> decisions) throws IllegalArgumentException {
    this.decisions = decisions;

    List<Choice> choices = new ArrayList<>();
    Map<Choice, String> choiceOptions = new HashMap<>();
    for (Choice choice : choiceOrder) {
      StringBuilder choiceId = new StringBuilder("C").append(choiceOrder.indexOf(choice))
          .append(" [ ");
      for (String decisionId : choiceOptionMap.get(choice)) {
        choiceId.append(decisionId).append(" ");
      }
      choiceId.append("]");
      choiceOptions.put(choice, choiceId.toString());
      choices.add(choice);
    }
    this.choices = choices;
    this.choiceOptions = choiceOptions;
  }

  /**
   * Gets all choices and decisions in the given story, returning them in the format of {@link
   * StoryNodes}. Uses a breadth-first pattern to visit all choice nodes, which might be
   * duplicated.
   *
   * @param story the story to go through
   * @return the choices and decisions of the story
   * @throws IllegalArgumentException if the given story is null
   */
  public static StoryNodes createNodes(StoryGame story) throws IllegalArgumentException {
    Utils.ensureNotNull(story, "Story can't be null");
    Map<Choice, List<String>> seenChoices = new HashMap<>();
    List<Choice> choiceOrder = new ArrayList<>();
    List<Decision> seenDecisions = new ArrayList<>();
    List<Choice> worklist = new LinkedList<>();
    worklist.add(story.getCurrentChoice()); // add the first choice to the worklist

    // While some choices are unvisited,
    while (worklist.size() > 0) {
      List<Choice> tempWorklist = new LinkedList<>();
      // visit each choice in the worklist
      for (Choice next : worklist) {
        // and if the choice is unseen, add it
        if (!seenChoices.containsKey(next)) {
          seenChoices.put(next, new ArrayList<>());
          choiceOrder.add(next);
          // and any unseen decisions
          for (Decision decision : next.getOptions()) {
            if (!seenDecisions.contains(decision)) {
              seenDecisions.add(decision);
            }
            seenChoices.get(next).add("D" + seenDecisions.indexOf(decision));

            List<Choice> outcomes = decision.getPossibleOutcomes();
            for (Choice outcome : outcomes) {
              if (!seenChoices.containsKey(outcome)) {
                tempWorklist.add(outcome);
              }
            }
          }
        }
      }
      worklist.clear();
      worklist.addAll(tempWorklist);
    }

    return new StoryNodes(seenChoices, choiceOrder, seenDecisions);
  }

  /**
   * Returns a list of all choices in the story.
   *
   * @return the list of choices
   */
  public List<Choice> getChoices() {
    return choices;
  }

  /**
   * Returns a map of choices to their option descriptions, in the format C# [ D# ... ].
   *
   * @return the map
   */
  public Map<Choice, String> getChoiceOptions() {
    return choiceOptions;
  }

  /**
   * Returns a list of all decisions in the story.
   *
   * @return the list of decisions
   */
  public List<Decision> getDecisions() {
    return decisions;
  }
}

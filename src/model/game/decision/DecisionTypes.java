package model.game.decision;

/**
 * An enumeration of all decision types.
 * <ul>
 *   <li>SIMPLE: a decision with an outcome</li>
 *   <li>CONSEQUENTIAL: a decision with an outcome that impacts the story's statuses</li>
 *   <li>DEPENDENT: a decision whose outcome is dependent on the story's statuses</li>
 * </ul>
 */
public enum DecisionTypes {
  SIMPLE, CONSEQUENTIAL, DEPENDENT
}

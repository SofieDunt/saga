package model.game.decision;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import model.game.Choice;
import utils.Utils;

/**
 * An {@link OutcomeDeterminer} that results in one outcome if one story status is greater than or
 * equal to some threshold, and another outcome if otherwise.
 */
public class TwoThresholdDeterminer implements OutcomeDeterminer {

  private final String dependency;
  private final int threshold;
  private final Choice below;
  private final Choice meets;

  /**
   * Constructs a {@code TwoThresholdDeterminer} that determines one of the given outcomes based on
   * the given status and threshold.
   *
   * @param dependency the status the determiner is based on
   * @param threshold  the threshold to result in one outcome or another
   * @param below      the outcome if the status does not meet the threshold
   * @param meets      the outcome if the status meets the outcome
   * @throws IllegalArgumentException if any argument is null
   */
  public TwoThresholdDeterminer(String dependency, int threshold, Choice below, Choice meets)
      throws IllegalArgumentException {
    Utils.ensureNotNull(dependency, "Dependency can't be null");
    Utils.ensureNotNull(below, "Choice can't be null");
    Utils.ensureNotNull(meets, "Choice can't be null");
    this.dependency = dependency;
    this.threshold = threshold;
    this.below = below;
    this.meets = meets;
  }

  @Override
  public Choice getOutcome(Map<String, Integer> statuses) throws IllegalArgumentException {
    if (!statuses.containsKey(this.dependency)) {
      throw new IllegalArgumentException(
          "Can't determine outcome of this story - missing status " + this.dependency);
    }

    if (statuses.get(this.dependency) < this.threshold) {
      return this.below;
    } else {
      return this.meets;
    }
  }

  @Override
  public String export(Map<Choice, String> choiceRepresentations) {
    Utils.ensureNotNull(choiceRepresentations, "Choice representations can't be null");
    if (!choiceRepresentations.containsKey(this.below) || !choiceRepresentations
        .containsKey(this.meets)) {
      throw new IllegalArgumentException("Map doesn't contain all outcomes");
    }

    return "TWOTHRESHOLD \"" + this.dependency + "\" " + this.threshold + " " + choiceRepresentations
        .get(this.below) + " " + choiceRepresentations.get(this.meets);
  }

  @Override
  public List<Choice> getPossibleOutcomes() {
    return new ArrayList<>(Arrays.asList(this.below, this.meets));
  }

  @Override
  public List<String> getDependency() {
    return Collections.singletonList(this.dependency);
  }
}

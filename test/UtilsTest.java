import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.junit.Test;
import utils.Utils;

public class UtilsTest {

  @Test
  public void ensureNotNull() {
    Random rand = new Random();

    for (int i = 0; i < 100; i++) {
      String testString = "test";
      String msg = "noException";
      boolean isNull = rand.nextBoolean();
      if (isNull) {
        testString = null;
      }
      try {
        assertEquals("test", Utils.ensureNotNull(testString, "No nulls"));
      } catch (IllegalArgumentException e) {
        msg = e.getMessage();
      }

      if (isNull) {
        assertEquals("No nulls", msg);
      }
    }

    try {
      Utils.ensureNotNull(null, "");
    } catch (IllegalArgumentException e) {
      assertEquals("", e.getMessage());
    }

    try {
      Utils.ensureNotNull(null, null);
    } catch (IllegalArgumentException e) {
      assertEquals("Can't be null!", e.getMessage());
    }
  }

  @Test
  public void removeNulls() {
    List<String> emptyList = new ArrayList<>();
    List<Integer> someNullsList = new ArrayList<>(Arrays.asList(1, 2, null, 4, 5, null, 7));
    List<Boolean> allNullsList = new ArrayList<>(Arrays.asList(null, null, null));
    List<Double> perfectList = new ArrayList<>(Arrays.asList(0.0, 1.1, 2.2));

    String msg = "noException";
    try {
      Utils.removeNulls(null);
    } catch (IllegalArgumentException e) {
      msg = e.getMessage();
    }
    assertEquals("List can't be null!", msg);
    assertEquals(new ArrayList<>(), emptyList);
    assertEquals(new ArrayList<>(Arrays.asList(1, 2, 4, 5, 7)), Utils.removeNulls(someNullsList));
    assertEquals(new ArrayList<>(), Utils.removeNulls(allNullsList));
    assertEquals(new ArrayList<>(Arrays.asList(0.0, 1.1, 2.2)), Utils.removeNulls(perfectList));
  }
}

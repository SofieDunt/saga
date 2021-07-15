import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import org.junit.Before;
import org.junit.Test;
import utils.Library;
import utils.MapLibrary;

/**
 * Tests for {@link MapLibrary}s.
 */
public class MapLibraryTest {

  private final Library<Integer> intLib = new MapLibrary<>(s -> "Can't be null", s -> "No " + s,
      s -> s + " exists");

  @Before
  public void addData() {
    intLib.add("one", 1);
    intLib.add("one", 1);
  }

  @Test
  public void add() {
    assertEquals(Arrays.asList("one", "one(1)"), intLib.getAllNames());
    assertEquals(1, (int) intLib.retrieve("one"));
    assertEquals(1, (int) intLib.retrieve("one(1)"));
  }

  @Test
  public void remove() {
    intLib.remove("one(1)");
    assertEquals(Collections.singletonList("one"), intLib.getAllNames());
    assertEquals(1, (int) intLib.retrieve("one"));
  }

  @Test
  public void removeNoSuch() {
    String msg = null;
    try {
      intLib.remove("none");
    } catch (IllegalArgumentException e) {
      msg = e.getMessage();
    }
    assertEquals("No none", msg);
    try {
      intLib.remove("does not exist");
    } catch (IllegalArgumentException e) {
      msg = e.getMessage();
    }
    assertEquals("No does not exist", msg);
    try {
      intLib.remove(null);
    } catch (IllegalArgumentException e) {
      msg = e.getMessage();
    }
    assertEquals("No null", msg);
  }

  @Test
  public void rename() {
    intLib.rename("one(1)", "two");
    assertEquals(Arrays.asList("one", "two"), intLib.getAllNames());
    assertEquals(1, (int) intLib.retrieve("one"));
    assertEquals(1, (int) intLib.retrieve("two"));
  }

  @Test
  public void renameNoSuch() {
    String msg = null;
    try {
      intLib.rename("none", "new");
    } catch (IllegalArgumentException e) {
      msg = e.getMessage();
    }
    assertEquals("No none", msg);
    try {
      intLib.rename("does not exist", "new");
    } catch (IllegalArgumentException e) {
      msg = e.getMessage();
    }
    assertEquals("No does not exist", msg);
    try {
      intLib.rename(null, "new");
    } catch (IllegalArgumentException e) {
      msg = e.getMessage();
    }
    assertEquals("No null", msg);
  }

  @Test
  public void renameToInvalid() {
    String msg = null;
    try {
      intLib.rename("one", "one");
    } catch (IllegalArgumentException e) {
      msg = e.getMessage();
    }
    assertEquals("one exists", msg);
    try {
      intLib.rename("one", "one(1)");
    } catch (IllegalArgumentException e) {
      msg = e.getMessage();
    }
    try {
      intLib.rename("one", null);
    } catch (IllegalArgumentException e) {
      msg = e.getMessage();
    }
    assertEquals("null exists", msg);
  }

  @Test
  public void update() {
    intLib.rename("one(1)", "two");
    intLib.update("two", 2);
    assertEquals(1, (int) intLib.retrieve("one"));
    assertEquals(2, (int) intLib.retrieve("two"));
  }

  @Test
  public void updateNoSuch() {
    String msg = null;
    try {
      intLib.update("none", 0);
    } catch (IllegalArgumentException e) {
      msg = e.getMessage();
    }
    assertEquals("No none", msg);
    try {
      intLib.update("does not exist", 0);
    } catch (IllegalArgumentException e) {
      msg = e.getMessage();
    }
    assertEquals("No does not exist", msg);
    try {
      intLib.update(null, 0);
    } catch (IllegalArgumentException e) {
      msg = e.getMessage();
    }
    assertEquals("No null", msg);
  }

  @Test
  public void updateNull() {
    String msg = null;
    try {
      intLib.update("one", null);
    } catch (IllegalArgumentException e) {
      msg = e.getMessage();
    }
    assertEquals("Can't be null", msg);
  }

  @Test
  public void getAllNames() {
    assertEquals(new ArrayList<>(), new MapLibrary<String>(null, null, null).getAllNames());
  }
}

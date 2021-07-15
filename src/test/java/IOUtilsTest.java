import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.StringReader;
import java.util.Scanner;
import org.junit.Test;
import utils.IOUtils;

/**
 * Tests for methods provided by {@link IOUtils}.
 */
public class IOUtilsTest {

  @Test
  public void tryNext() {
    Scanner sc = new Scanner(new StringReader("hi world \"it's me\" \"\"friend\"\""));
    assertEquals("hi", IOUtils.tryNext(sc, "bad"));
    assertEquals("world", IOUtils.tryNext(sc, "bad"));
    assertEquals("it's me", IOUtils.tryNext(sc, "bad"));
    assertEquals("\"friend\"", IOUtils.tryNext(sc, "bad"));
    String msg = "";
    try {
      IOUtils.tryNext(sc, "bad");
    } catch (IllegalArgumentException e) {
      msg = e.getMessage();
    }
    assertEquals(msg, "bad");
    try {
      IOUtils.tryNext(sc, null);
    } catch (IllegalArgumentException e) {
      msg = e.getMessage();
    }
    assertNull(msg);
    try {
      IOUtils.tryNext(null, "bad");
    } catch (IllegalArgumentException e) {
      msg = e.getMessage();
    }
    assertEquals(msg, "Scanner can't be null");
  }

  @Test
  public void tryNextInt() {
    Scanner sc = new Scanner(new StringReader("2 21 00 -10 -010 nope bad"));
    assertEquals(2, IOUtils.tryNextInt(sc, "bad"));
    assertEquals(21, IOUtils.tryNextInt(sc, "bad"));
    assertEquals(0, IOUtils.tryNextInt(sc, "bad"));
    assertEquals(-10, IOUtils.tryNextInt(sc, "bad"));
    assertEquals(-10, IOUtils.tryNextInt(sc, "bad"));
    String msg = "";
    try {
      IOUtils.tryNextInt(sc, "bad");
    } catch (IllegalArgumentException e) {
      msg = e.getMessage();
    }
    assertEquals(msg, "bad");
    try {
      IOUtils.tryNextInt(sc, null);
    } catch (IllegalArgumentException e) {
      msg = e.getMessage();
    }
    assertNull(msg);
    try {
      IOUtils.tryNextInt(null, "bad");
    } catch (IllegalArgumentException e) {
      msg = e.getMessage();
    }
    assertEquals(msg, "Scanner can't be null");
  }

  @Test
  public void getNumId() {
    assertEquals(0, IOUtils.getNumId("C0", ""));
    assertEquals(11, IOUtils.getNumId("111", ""));
    assertEquals(-1, IOUtils.getNumId("C-1", ""));
    assertEquals(10, IOUtils.getNumId("z10", ""));
    assertEquals(29, IOUtils.getNumId("f29", ""));
  }

  @Test
  public void getNumIdInvalidId() {
    String msg = "";
    try {
      IOUtils.getNumId("Co", "bad");
    } catch (IllegalArgumentException e) {
      msg = e.getMessage();
    }
    assertEquals("bad", msg);
    try {
      IOUtils.getNumId("f2o9", "wrong");
    } catch (IllegalArgumentException e) {
      msg = e.getMessage();
    }
    assertEquals("wrong", msg);
    try {
      IOUtils.getNumId("00o", "not right");
    } catch (IllegalArgumentException e) {
      msg = e.getMessage();
    }
    assertEquals("not right", msg);
  }

  @Test
  public void getNumIdNulls() {
    // Null stringId
    String msg = "";
    try {
      IOUtils.getNumId(null, "msg");
    } catch (IllegalArgumentException e) {
      msg = e.getMessage();
    }
    assertEquals("ID can't be null", msg);

    // Null msg
    msg = "";
    try {
      IOUtils.getNumId("", null);
    } catch (IllegalArgumentException e) {
      msg = e.getMessage();
    }
    assertNull(msg);
  }

  @Test
  public void pathSeparator() {
    if (System.getProperty("os.name").startsWith("Windows")) {
      assertEquals('\\', IOUtils.pathSeparator());
    } else {
      assertEquals('/', IOUtils.pathSeparator());
    }
  }
}

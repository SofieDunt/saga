import static org.junit.Assert.assertEquals;

import java.io.StringReader;
import java.util.Random;
import java.util.Scanner;
import model.game.statusUpdate.AddStatus;
import model.game.statusUpdate.SetStatus;
import model.game.statusUpdate.StatusUpdateCreator;
import model.game.statusUpdate.StatusUpdateTypes;
import org.junit.Test;

/**
 * Tests for {@link model.game.statusUpdate.StatusUpdate}s and {@link
 * StatusUpdateCreator}s.
 */
public class StatusUpdateTests {

  @Test
  public void testUpdate() {
    Random rand = new Random();
    for (int i = 0; i < 50; i++) {
      int val = rand.nextInt();
      int val2 = rand.nextInt();
      assertEquals(val + val2, new AddStatus(val).update(val2));
      assertEquals(val, new SetStatus(val).update(val2));
    }
  }

  @Test
  public void testExport() {
    Random rand = new Random();
    for (int i = 0; i < 50; i++) {
      int val = rand.nextInt();
      assertEquals("ADD " + val, new AddStatus(val).export());
      assertEquals("SET " + val, new SetStatus(val).export());
    }
  }

  @Test
  public void testImportSimple() {
    Random rand = new Random();
    for (int i = 0; i < 50; i++) {
      int val = rand.nextInt();
      int val2 = rand.nextInt();
      Scanner addImport = new Scanner(new StringReader("ADD " + val));
      Scanner setImport = new Scanner(new StringReader("SET " + val));
      assertEquals(val + val2, StatusUpdateCreator.importSimple(addImport).update(val2));
      assertEquals(val, StatusUpdateCreator.importSimple(setImport).update(val2));
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void testImportSimpleNullScanner() {
    StatusUpdateCreator.importSimple(null);
  }

  @Test
  public void testCreateSimple() {
    Random rand = new Random();
    for (int i = 0; i < 50; i++) {
      int val = rand.nextInt();
      assertEquals(new AddStatus(val).export(),
          StatusUpdateCreator.createSimple(StatusUpdateTypes.ADD, val).export());
      assertEquals(new SetStatus(val).export(),
          StatusUpdateCreator.createSimple(StatusUpdateTypes.SET, val).export());
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateSimpleNullType() {
    StatusUpdateCreator.createSimple(null, 4);
  }
}

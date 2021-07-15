import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import io.TextExporter;
import io.TextImporter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import model.game.StoryGame;
import org.junit.Test;
import utils.IOUtils;

/**
 * Tests for {@link TextImporter} and {@link TextExporter}.
 */
public class TextImporterExporterTest {

  @Test
  public void testImportExport() throws IOException {
    String exportedPath = "./src/test/resources/ioTest/right1.txt";
    String importedPath = "./src/test/resources/ioTest/right2.txt";

    new TextExporter().export(TestDataProvider.goRight(), exportedPath);
    StoryGame storyGame = new TextImporter().importStory(exportedPath);
    new TextExporter().export(storyGame, importedPath);

    Scanner sc1 = new Scanner(new FileInputStream(exportedPath));
    Scanner sc2 = new Scanner(new FileInputStream(importedPath));

    while (sc1.hasNext()) {
      assertTrue(sc2.hasNext());
      assertEquals(sc1.next(), sc2.next());
    }
  }

  @Test
  public void testImportExportDependent() throws IOException {
    String exportedPath = "./src/test/resources/ioTest/strength1.txt";
    String importedPath = "./src/test/resources/ioTest/strength2.txt";

    new TextExporter().export(TestDataProvider.strengthStory(), exportedPath);
    StoryGame storyGame = new TextImporter().importStory(exportedPath);
    new TextExporter().export(storyGame, importedPath);

    Scanner sc1 = new Scanner(new FileInputStream(exportedPath));
    Scanner sc2 = new Scanner(new FileInputStream(importedPath));

    while (sc1.hasNext()) {
      assertTrue(sc2.hasNext());
      assertEquals(sc1.next(), sc2.next());
    }

    assertEquals(
        "get 1 strength(1), get 2 strength(2), get 3 strength(3), or don't get strength(4)",
        storyGame.getCurrentChoice().toString());
    storyGame.next(0);
    assertEquals("win", storyGame.getCurrentChoice().toString());
    storyGame = new TextImporter().importStory(exportedPath);
    storyGame.next(1);
    assertEquals("lose", storyGame.getCurrentChoice().toString());
    storyGame = new TextImporter().importStory(exportedPath);
    storyGame.next(2);
    assertEquals("lose", storyGame.getCurrentChoice().toString());
    storyGame = new TextImporter().importStory(exportedPath);
    storyGame.next(3);
    assertEquals("win", storyGame.getCurrentChoice().toString());
  }

  @Test
  public void testIllegalExportPath() throws IOException {
    String msg = "No exception";

    try {
      new TextExporter().export(TestDataProvider.goRight(), "noPath/.none./fail");
    } catch (IllegalArgumentException e) {
      msg = e.getMessage();
    }

    assertEquals("Can't write to path: noPath/.none./fail", msg);
  }

  @Test
  public void testNullExportPath() throws IOException {
    File file = new TextExporter().export(TestDataProvider.goRight(), null);
    String msg = "No exception";
    try {
      new Scanner(new FileInputStream("./Go Right!.txt"));
    } catch (IOException e) {
      msg = e.getMessage();
    }
    assertEquals("No exception", msg);
    assertTrue(file.delete());
  }

  @Test
  public void testImportFileNotFound() {
    String msg = "No exception";
    try {
      new TextImporter().importStory("./res/none.txt");
    } catch (IllegalArgumentException e) {
      msg = e.getMessage();
    }
    assertEquals("File not found", msg);
  }

  @Test
  public void testImportInvalidFormats() {
    char ps = IOUtils.pathSeparator();
    String dirPath = "./src/test/resources/";
    String msg = "No exception";
    List<String> badFileNames = new ArrayList<>(Arrays
        .asList("badImports/badCq.txt", "badImports/badSU.txt", "badImports/empty.txt",
            "badImports/invalidDType.txt", "badImports/invalidSUType.txt",
            "badImports/missingChar.txt", "badImports/missingNum.txt",
            "badImports/noInitStatus.txt", "badImports/noNumChoices.txt"));

    for (String name : badFileNames) {
      try {
        new TextImporter().importStory(dirPath + name);
      } catch (IllegalArgumentException e) {
        msg = e.getMessage();
      }
      assertEquals("Illegal format", msg.substring(0, 14));
      msg = "No exception";
    }
  }
}

package murusgallicus.testutils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Objects;

public class TestUtils {

  public static BufferedReader loadTestData(String filename) {
    BufferedReader reader = null;
    try {
      File file = new File(
          Objects.requireNonNull(TestUtils.class.getClassLoader().getResource(filename)).getFile());
      reader = new BufferedReader(new FileReader(file));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    assert reader != null;

    return reader;
  }

}

package org.sa;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

/**
 * Loop through all the .txt files in a single directory.
 * Count how many lines all these files have combined.
 **/
public class C_TxtFileLineCounter {
  public static void main(String[] args) {

    String directoryPath = "src/main/java/org/sa/storage/b_filtered";

    try (Stream<Path> filePathStream = Files.walk(Paths.get(directoryPath))) {
      long totalLines = filePathStream
          .filter(Files::isRegularFile)
          .filter(path -> path.toString().endsWith(".txt"))
          .mapToLong(C_TxtFileLineCounter::countLinesInTxtFile)
          .sum();

      System.out.println("Total number of lines in all .txt files: " + totalLines);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static long countLinesInTxtFile(Path txtFilePath) {
    try (Stream<String> lines = Files.lines(txtFilePath)) {
      return lines.count();
    } catch (IOException e) {
      e.printStackTrace();
      return 0;
    }
  }
}

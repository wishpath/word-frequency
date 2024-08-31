package org.sa;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.GZIPInputStream;
import java.util.stream.Stream;
/**
 * Loop all the  gz files in a single directory. Count how many lines all these files have combined
 * **/
public class A_GzFileLineCounter {
  public static void main(String[] args) {

    String directoryPath = "src/main/java/org/sa/storage/a_downloaded";

    try (Stream<Path> filePathStream = Files.walk(Paths.get(directoryPath))) {
      long totalLines = filePathStream
          .filter(Files::isRegularFile)
          .filter(path -> path.toString().endsWith(".gz"))
          .mapToLong(A_GzFileLineCounter::countLinesInGzFile)
          .sum();

      System.out.println("Total number of lines in all .gz files: " + totalLines);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static long countLinesInGzFile(Path gzFilePath) {
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(
        new GZIPInputStream(Files.newInputStream(gzFilePath))))) {
      return reader.lines().count();
    } catch (IOException e) {
      e.printStackTrace();
      return 0;
    }
  }
}


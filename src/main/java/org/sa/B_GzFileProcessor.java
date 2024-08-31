package org.sa;

import java.io.*;
import java.nio.file.*;
import java.util.zip.GZIPInputStream;
import java.util.stream.Stream;

/**
 * From path A read all the gz files line by line.
 * For each line ask if (isGoodWord(line)).
 * If the method returns true, construct a new line by method String newLine = constructNewLine(line).
 * Write / append this new line to a file 1.txt in the path B.
 * When 1.txt will reach 10000 lines consider it is full and start writing to 2.txt and so on.
 **/

public class B_GzFileProcessor {

  private static final int MAX_LINES = 10000;

  public static void main(String[] args) {
    Path inputDir = Paths.get("src/main/java/org/sa/storage/a_downloaded");
    Path outputDir = Paths.get("src/main/java/org/sa/storage/b_filtered");

    try {
      processFiles(inputDir, outputDir);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static void processFiles(Path inputDir, Path outputDir) throws IOException {
    int fileCounter = 1;
    int lineCounter = 0;
    BufferedWriter writer = newBufferedWriter(outputDir, fileCounter);

    try (Stream<Path> paths = Files.walk(inputDir)) {
      for (Path path : (Iterable<Path>) paths.filter(Files::isRegularFile).filter(p -> p.toString().endsWith(".gz"))::iterator) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(Files.newInputStream(path))))) {
          String line;
          while ((line = reader.readLine()) != null) {
            if (isGoodWord(line)) {
              String newLine = constructNewLine(line);
              writer.write(newLine);
              writer.newLine();
              lineCounter++;

              if (lineCounter == MAX_LINES) {
                writer.close();
                fileCounter++;
                writer = newBufferedWriter(outputDir, fileCounter);
                lineCounter = 0;
              }
            }
          }
        }
      }
    } finally {
      if (writer != null) {
        writer.close();
      }
    }
  }

  private static BufferedWriter newBufferedWriter(Path outputDir, int fileCounter) throws IOException {
    Path outputFilePath = outputDir.resolve(fileCounter + ".txt");
    return Files.newBufferedWriter(outputFilePath, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
  }

  private static boolean isGoodWord(String line) {
    if (line == null) return false;
    if (line.equals("")) return false;
    if (!line.contains("2018")) return false;
    String[] arr = line.split("\t");
    if (arr.length == 0) return false;
    String[] wordParts = arr[0].split("_");
    if (wordParts.length == 0) return false;
    String word = wordParts[0];
    if (word.matches(".*[^a-zA-Z].*")) return false;
    long useCount = getUseCount(arr);
    if (useCount < 1000L) return false;
    return true;
  }

  private static String constructNewLine(String line) {
    String[] arr = line.split("\t");
    return arr[0].split("_")[0].toLowerCase() + "\t" + getUseCount(arr);
  }

  private static long getUseCount(String[] arr) {
    for (String s : arr) if (s.matches("2018,\\d+,\\d+")) {
      String useCount = s.replaceAll(".*2018,(\\d+),\\d+.*", "$1");
      if (useCount.matches("\\d+")) return Long.parseLong(useCount);
    }
    return -1;
  }
}

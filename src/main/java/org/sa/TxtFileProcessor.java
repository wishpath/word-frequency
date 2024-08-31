package org.sa;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

public class TxtFileProcessor {
  private static final Path inputPath = Paths.get("src/main/java/org/sa/storage/b_filtered");
  private static final Path outputPath = Paths.get("src/main/java/org/sa/storage/d_done");
  private static final int outputWordsLimit = 200000;


  public static void main(String[] args) throws IOException {
    Map<String, Long> wordCountMap = new HashMap<>();
    readAllTxtFiles(inputPath, wordCountMap);
    List<Map.Entry<String, Long>> sortedEntries = sortAndLimitMap(wordCountMap);
    writeToFile(outputPath.resolve("word_frequency.txt"), sortedEntries, true);
    writeToFile(outputPath.resolve("words.txt"), sortedEntries, false);
  }

  private static void readAllTxtFiles(Path inputPath, Map<String, Long> wordCountMap) {
    try (Stream<Path> filePathStream = Files.walk(inputPath)) {
      filePathStream
          .filter(Files::isRegularFile)
          .filter(path -> path.toString().endsWith(".txt"))
          .forEach(path -> processFile(path, wordCountMap));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static List<Map.Entry<String, Long>> sortAndLimitMap(Map<String, Long> wordCountMap) {
    List<Map.Entry<String, Long>> sortedEntries = wordCountMap.entrySet()
        .stream()
        .sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue()))
        .limit(outputWordsLimit)
        .collect(Collectors.toList());
    return sortedEntries;
  }

  private static void processFile(Path filePath, Map<String, Long> wordCountMap) {
    try (BufferedReader reader = Files.newBufferedReader(filePath)) {
      String line;
      while ((line = reader.readLine()) != null) {
        if (!isLineValid(line)) continue;

        String[] arr = line.split("\t");
        if (arr.length < 2) continue;

        String word = arr[0];
        long count = Long.parseLong(arr[1]);

        wordCountMap.merge(word, count, Long::sum);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static boolean isLineValid(String line) {
    return line != null && line.contains("\t") && line.matches("[a-zA-Z]+\\t\\d+");
  }

  private static void writeToFile(Path filePath, List<Map.Entry<String, Long>> entries, boolean includeValues) throws IOException {
    try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
      for (Map.Entry<String, Long> entry : entries) {
        if (includeValues) {
          writer.write(entry.getKey() + "\t" + entry.getValue());
        } else {
          writer.write(entry.getKey());
        }
        writer.newLine();
      }
    }
  }
}


package com.matching.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Logger {
  private static final String LOG_FILE_PATH = "src/main/resources/app.log";

  static {
    try {
      File logFile = new File(LOG_FILE_PATH);
      Files.createDirectories(Paths.get(logFile.getParent()));
      if (logFile.exists()) {
        new PrintWriter(logFile).close();
      } else {
        logFile.createNewFile();
      }
    } catch (IOException e) {
      System.err.println("Failed to initialize logger: " + e.getMessage());
    }
  }

  public static void log(String info) {
    writeLog(info);
  }

  public static void log(String info, Object... args) {
    writeLog(String.format(info, args));
  }

  private static void writeLog(String message) {
    try (FileWriter fileWriter = new FileWriter(LOG_FILE_PATH, true);
         PrintWriter printWriter = new PrintWriter(fileWriter)) {
      printWriter.println(message);
//      System.out.println(message);
    } catch (IOException e) {
      System.err.println("Failed to write log: " + e.getMessage());
    }
  }
}

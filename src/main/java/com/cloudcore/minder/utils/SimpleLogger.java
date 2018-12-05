package com.cloudcore.minder.utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.cloudcore.minder.core.FileSystem;


public class SimpleLogger {
	/* Fields */

    private static String fullFilePath = FileSystem.LogsFolder;


    public static void writeLog(String logFileDetails) {
        String filepath = fullFilePath + "minderLog";
        String finalFilepath = filepath + ".log";
        int counter = 0;

        for (int i = 0; i < 10; i++) {
            try {
                Path path = Paths.get(finalFilepath);
                if (!Files.exists(path)) {
                    Files.createDirectories(path.getParent());
                    Files.createFile(path);
                }
                Files.write(path, logFileDetails.getBytes(StandardCharsets.UTF_8));
                break;
            } catch (IOException e) {
                finalFilepath = filepath + '.' + counter + ".log";
            }
            counter++;
        }
    }
}

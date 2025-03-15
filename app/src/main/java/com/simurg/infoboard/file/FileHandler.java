package com.simurg.infoboard.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;

public class FileHandler {
    public static String readFromFile(File file) throws FileNotFoundException {

try(BufferedReader bufferedReader= new BufferedReader(new FileReader(file))){
    StringBuilder content = new StringBuilder();
    int charCount = 0;
    int limit = 1000;
    String line;
    while ((line = bufferedReader.readLine()) != null && charCount < limit) {
        content.append(line);
        charCount += line.length();
        if (charCount >= limit) {
            break;
        }
    }
    return line;
} catch (IOException e) {
    throw new RuntimeException(e);
}

    }
}

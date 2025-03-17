package com.simurg.infoboard.file;

import com.simurg.infoboard.log.FileLogger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;

public class FileHandler {
    public static String readFromFile(File file) throws FileNotFoundException {
StringBuilder fileContent= new StringBuilder();
       try(Scanner scanner= new Scanner(file)) {
           while (scanner.hasNextLine()){
               fileContent.append(scanner.nextLine());
           }
       }
       FileLogger.log("readFromFile", "scanner closed, registration info: "+ fileContent);
return fileContent.toString();
    }
}

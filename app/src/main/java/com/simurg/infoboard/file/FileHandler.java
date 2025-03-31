package com.simurg.infoboard.file;

import com.simurg.infoboard.log.FileLogger;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.util.Date;
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
    public static boolean deleteFile(File file) {
        if (file != null && file.isFile()) {
            return file.delete(); // Удаляет файл, если это действительно файл
        }
        return false; // Если это не файл, возвращаем false
    }
public static long getFileSize(File file){
        return file.length();
}
public static long lastModified(File file){
   return file.lastModified();
    }
}

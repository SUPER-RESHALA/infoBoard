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
    public static File createCustomFolder(File parentFolder, String customFolderName) {
        if (parentFolder == null || !parentFolder.exists()) {
            FileLogger.logError("createCustomFolder(without context)", "base folder is already exist or not specified");
            return null;
        }

        File customFolder = new File(parentFolder, customFolderName); // Создаём папку внутри указанной родительской

        if (!customFolder.exists()) {
            boolean created = customFolder.mkdirs(); // Создание папки
            if (created) {
                FileLogger.log("createCustomFolder(without context)", "mkdir success "+ customFolder.getAbsolutePath());
            } else {
                FileLogger.logError("createCustomFolder(without context)", "cant mkdir "+ customFolder.getAbsolutePath());
            }
        } else {
            FileLogger.log("createCustomFolder(without context)", "Dir already exists "+ customFolder.getAbsolutePath());
        }

        return customFolder;
    }

}

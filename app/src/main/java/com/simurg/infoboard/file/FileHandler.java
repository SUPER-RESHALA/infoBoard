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
    public static boolean renameFileWithReplace(String originalFilePath, String newFileName) {
        File originalFile = new File(originalFilePath);

        // Проверяем, существует ли исходный файл
        if (!originalFile.exists()) {
            FileLogger.logError("renameFileWithReplace", "File Not found "+ originalFilePath);
            return false;
        }
        if (originalFile.getName().equals(newFileName)) {
            FileLogger.log("renameFileWithReplace", "Original and new file names are the same. Skipping rename.");
            return true; // или false, если ты хочешь считать это ошибкой
        }
        // Получаем родительскую директорию исходного файла
        File parentDir = originalFile.getParentFile();

        // Создаём объект File для нового имени в той же директории
        File newFile = new File(parentDir, newFileName);

        // Если файл с новым именем уже существует, удаляем его
        if (newFile.exists()) {
            if (!newFile.delete()) {
                FileLogger.logError("renameFileWithReplace", "Cant delete existing file "+ newFile.getAbsolutePath());
                return false;
            }
        }

        // Переименовываем файл
        boolean success = originalFile.renameTo(newFile);
        if (success) {
            FileLogger.log("renameFileWithReplace", "renameSuccess "+ newFile.getAbsolutePath());
        } else {
            FileLogger.logError("renameFileWithReplace", "Cant rename File "+originalFilePath );
        }

        return success;
    }
}

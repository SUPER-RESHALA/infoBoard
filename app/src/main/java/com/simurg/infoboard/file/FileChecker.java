package com.simurg.infoboard.file;

import java.io.File;

public class FileChecker {
    public static boolean isFileExist(File file){
        return file.exists();
    }
    public static boolean isFileExist(File folder, String filename){
        return new File(folder,filename).exists();
    }

}

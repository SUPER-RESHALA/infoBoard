package com.simurg.infoboard.ftp;

import android.util.Log;

import com.simurg.infoboard.log.FileLogger;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

public class FtpFileManager {
    private final FTPClient ftpClient;

    public FtpFileManager(FTPClient ftpClient) {
        this.ftpClient = ftpClient;
    }

    public boolean downloadFile(String remoteFileName, String localFilePath) {
        try (FileOutputStream fos = new FileOutputStream(new File(localFilePath))) {
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            boolean success = ftpClient.retrieveFile(remoteFileName, fos);
            FileLogger.log("downloadFtp", "name "+remoteFileName+" LocPath"+localFilePath+ " success "+success);
            return success;
        } catch (IOException e) {
            FileLogger.logError("downloadFtp", "Error download file: " + e.getMessage());
            return false;
        }
    }
    public boolean fileExists(String fileName) {
        try {
            FTPFile[] files = ftpClient.listFiles(fileName); // Проверяем текущую директорию
            return files.length > 0 && files[0].isFile(); // Если найдено, проверяем, что это файл
        } catch (IOException e) {
            FileLogger.logError("fileExists","Error checking file exist " + e.getMessage() );
            return false;
        }
    }


    public long getFileSize(String fileName, FTPClient ftpClient) throws IOException {
        FTPFile[] files = ftpClient.listFiles(fileName);
        if (files.length == 1) {
            return files[0].getSize();
        }
        return -1; // Файл не найден
    }

    /**
     * Навигация к родительской директории.
     */
    public boolean navigateToParentDirectory() {
        try {
            boolean success = ftpClient.changeToParentDirectory();
            FileLogger.log("navigate to parent dir","boolean:  "+ success );
            return success;
        } catch (IOException e) {
            FileLogger.logError("navigate to parent dir", "Error up parent dir "+  e.getMessage());
            return false;
        }
    }

    public boolean uploadFile(String localFileName) {
        try (FileInputStream fis = new FileInputStream(new File(localFileName))) {
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            String remoteFileName = new File(localFileName).getName(); // Получаем имя файла
            boolean success = ftpClient.storeFile(remoteFileName, fis); // Используем только имя файла
            if (!success) {
                int replyCode = ftpClient.getReplyCode();
                String replyMessage = ftpClient.getReplyString();
                FileLogger.logError("UploadFtpFile", "Upload failed: FileName=" + localFileName
                        + ", ReplyCode=" + replyCode
                        + ", ReplyMessage=" + replyMessage);

            }
            FileLogger.log("UploadFtpFile", localFileName+ "Boolean "+success);
            return success;
        } catch (IOException e) {
            FileLogger.logError("FTPFileManager uploadFile", "Error UploadFtpFile "+ e.getMessage());
            return false;
        }
    }
    public boolean deleteFile(String fileName) {
        try {
            boolean success = ftpClient.deleteFile(fileName);
            FileLogger.log("deleteFtpFile", "Delete: "+ fileName+ "is "+ success);
            return success;
        } catch (IOException e) {
            FileLogger.logError("deleteFtpFile", "Error deleteFile "+e.getMessage());
            return false;
        }
    }
    public String getCurrentWorkingDirectory() {
        try {
            return ftpClient.printWorkingDirectory();
        } catch (IOException e) {
            FileLogger.logError("getCurrentWorkingDir", "Error get currentDir "+ e.getMessage());
            return null;
        }
    }
    public boolean changeWorkingDirectory(String remoteDirectoryPath) {
        try {
            boolean success = ftpClient.changeWorkingDirectory(remoteDirectoryPath);
            FileLogger.log("changeFtpDir","Cnage to "+ remoteDirectoryPath+ " is "+ success);
            return success;
        } catch (IOException e) {
            FileLogger.logError("changeFtpDir", "error Change working dir "+ e.getMessage());
            return false;
        }
    }

    public boolean moveCurrentDir(String path) throws IOException {
        if (!Objects.equals(getCurrentWorkingDirectory(),path)){
            FileLogger.log("moveCurrentDir","go to parent");
           navigateToParentDirectory();
         //while (navigateToParentDirectory());
       return changeWorkingDirectory(path);
        }
        FileLogger.log("moveCurrentDir", "success move to "+ path);
        return  true;
    }//end of method
}

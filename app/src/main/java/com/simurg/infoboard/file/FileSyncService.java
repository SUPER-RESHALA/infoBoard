package com.simurg.infoboard.file;

import com.simurg.infoboard.ftp.FtpFileInfo;
import com.simurg.infoboard.ftp.FtpFileManager;
import com.simurg.infoboard.item.MediaItem;
import com.simurg.infoboard.log.FileLogger;

import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.IOException;

public class FileSyncService {

    /**
     * Checks whether the JSON file has changed compared to the version on the FTP server.
     *
     * The method retrieves file information from the FTP server and compares its size and last modification
     * time with the local file.
     *
     * @param jsonFile the local JSON file to check.
     * @param ftpClient the FTP client for connecting to the server.
     * @return {@code true} if the file has changed (size or last modification time differs), otherwise {@code false}.
     * @throws IOException if an error occurs while retrieving information from the FTP server.
     */
    public boolean isJsonChanged(File jsonFile, FTPClient ftpClient) throws IOException {
        FtpFileInfo ftpFileInfo= FtpFileManager.getFileInfo(jsonFile.getName(),ftpClient);
        FileLogger.log("isJsonChanged",ftpFileInfo.toString()+"  FtpGetSize "+ftpFileInfo.getSize());
       return FileHandler.getFileSize(jsonFile) != ftpFileInfo.getSize() ||
               FileHandler.lastModified(jsonFile) != ftpFileInfo.getModificationTime().getTime();
    }

    public boolean isFilePresent(MediaItem mediaItem){
return mediaItem.getFile().exists();
    }
}

package com.simurg.infoboard.file;

import android.content.Context;

import com.fasterxml.jackson.core.JsonParseException;
import com.simurg.infoboard.config.Config;
import com.simurg.infoboard.ftp.FtpConnectionManager;
import com.simurg.infoboard.ftp.FtpFileInfo;
import com.simurg.infoboard.ftp.FtpFileManager;
import com.simurg.infoboard.item.MediaItem;
import com.simurg.infoboard.item.MediaItemHandler;
import com.simurg.infoboard.json.JSONHandler;
import com.simurg.infoboard.log.FileLogger;
import com.simurg.infoboard.utils.NetworkUtils;

import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

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
    public static boolean isJsonChanged(File jsonFile, FTPClient ftpClient) throws IOException {
        FtpFileInfo ftpFileInfo= FtpFileManager.getFileInfo(jsonFile.getName(),ftpClient);
        FileLogger.log("isJsonChanged",ftpFileInfo.toString()+"  FtpGetSize "+ftpFileInfo.getSize());
       return FileHandler.getFileSize(jsonFile) != ftpFileInfo.getSize() ||
               FileHandler.lastModified(jsonFile) != ftpFileInfo.getModificationTime().getTime();
    }

    public boolean isFilePresent(MediaItem mediaItem){
return mediaItem.getFile().exists();
    }
    public static boolean tmpToJson(File tmpFile){
        String newFilename=tmpFile.getName().replace(".tmp", ".json");
return FileHandler.renameFileWithReplace(tmpFile.getAbsolutePath(),newFilename);
    }
    public static String getTmpPath(File jsonFile, String jsonName){
        return  jsonFile.getParent()+"/"+jsonName+".tmp";
    }
    public static File downloadJsonFile(File jsonFile,Config config, Context context, FtpFileManager ftpFileManager){
        if(!NetworkUtils.isNetworkConnected(context)){return null;}
        File tmpFile= new File(getTmpPath(jsonFile,config.getJsonName()));
        try{
       ftpFileManager.downloadFile(config.getJsonName(),getTmpPath(jsonFile,config.getJsonName()));
        return tmpFile;
        }catch (IOException e){
FileLogger.logError("downloadJsonFile", "Exception "+ e.getMessage());
            boolean isTmpDel=false;
if (tmpFile.exists()){ isTmpDel=FileHandler.deleteFile(tmpFile);}
FileLogger.logError("downloadJsonFile","isTMPDeleted: "+isTmpDel);
return null;
        }
    }
    public static boolean updateJsonFromFtp(FtpConnectionManager ftpConnectionManager,FtpFileManager ftpFileManager, File jsonFile, Config config, Context context) throws IOException {
        if (isJsonChanged(jsonFile,ftpConnectionManager.getFtpClient())){
            File tmpFile=downloadJsonFile(jsonFile,config,context,ftpFileManager);
            if (tmpFile!=null&& tmpFile.exists()){
                if (tmpToJson(tmpFile)){
                    return true;
                }else {
                    FileLogger.logError("updateJson", "Error in tmpToJson");
                    return false;
                }
            }else {
                FileLogger.logError("updateJson", "Error: json null or not existed");
                return false;
            }
        }else {
            FileLogger.log("updateJson", "json not changed");
            return false;
        }
    }
//public  static boolean syncMediaFiles(File jsonFile, Config config, Context context){
//    FtpConnectionManager ftpConnectionManager= new FtpConnectionManager();
//    FtpFileManager ftpFileManager = new FtpFileManager(ftpConnectionManager.getFtpClient());
//    ftpConnectionManager.connect(config.getHost());
//    ftpConnectionManager.login(config.getUserName(),config.getPassword());
//    if(!NetworkUtils.isNetworkConnected(context)){return false;}
//        if (FileChecker.isFileExist(jsonFile)){
//            if (isJsonChanged(jsonFile,ftpConnectionManager.getFtpClient()))
//try{
//    //TODO подумать об соединении
//
//
//
//
//
//
//
//
//
//
//if (tmpFile!=null&& tmpFile.exists()){
// FileLogger.log("syncMediaTmpToJson", "isSuccess: "+ tmpToJson(tmpFile));
// List<Map<String,Object>> mediaList= JSONHandler.readJsonFromFile(jsonFile);
// //   MediaItemHandler.createMediaItem(mediaList, baseFolder);
//}else {
//    return false;
//}
//
//    ftpFileManager.changeWorkingDirectory(config.getMediaDirName());
//}catch (IOException e){
//FileLogger.logError("SyncMediaFiles","Exception: "+ e.getMessage());
//return false;
//}
//        }//JSON FILE EXISt IF END
//}

}

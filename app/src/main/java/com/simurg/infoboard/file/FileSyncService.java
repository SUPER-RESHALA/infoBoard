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
import com.simurg.infoboard.player.MediaPlayerManager;
import com.simurg.infoboard.utils.NetworkUtils;

import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public static boolean tmpToJson(File tmpFile, String tmpFileExtension){
        String newFilename=tmpFile.getName().replace(tmpFileExtension, ".json");
return FileHandler.renameFileWithReplace(tmpFile.getAbsolutePath(),newFilename);
    }
//    public static File tmpToJson(File tmpFile){
//        String newFilename=tmpFile.getName().replace(".tmp", ".json");
//         FileHandler.renameFileWithReplace(tmpFile.getAbsolutePath(),newFilename);
//         return new File(tmpFile.getParent(),newFilename);
//    }
    public static String getTmpPath(File jsonFile, String jsonName, String tmpFileExtension){
        return  jsonFile.getParent()+"/"+jsonName.replace(".json",tmpFileExtension);
    }
    public static File downloadJsonFile(File jsonFile,Config config, Context context, FtpFileManager ftpFileManager, String tmpFileExtension){
        if(!NetworkUtils.isNetworkConnected(context)){return null;}
        File tmpFile= new File(getTmpPath(jsonFile,config.getJsonName(),tmpFileExtension));
        try{
       ftpFileManager.downloadFile(config.getJsonName(),getTmpPath(jsonFile,config.getJsonName(),tmpFileExtension));
        return tmpFile;
        }catch (IOException e){
FileLogger.logError("downloadJsonFile", "Exception "+ e.getMessage());
            boolean isTmpDel=false;
if (tmpFile.exists()){ isTmpDel=FileHandler.deleteFile(tmpFile);}
FileLogger.logError("downloadJsonFile","isTMPDeleted: "+isTmpDel);
return null;
        }
    }
    public static boolean updateJsonFromFtp(FtpFileManager ftpFileManager, File jsonFile, Config config, Context context, String tmpFileExtension){
            File tmpFile=downloadJsonFile(jsonFile,config,context,ftpFileManager, tmpFileExtension);
            if (tmpFile!=null&& tmpFile.exists()){
                if (tmpToJson(tmpFile,tmpFileExtension)){
                    return true;
                }else {
                    FileLogger.logError("updateJson", "Error in tmpToJson");
                    return false;
                }
            }else {
                FileLogger.logError("updateJson", "Error: json null or not existed");
                return false;
            }

    }
//public static Optional<File> updateJsonFromFtpOptional(FtpFileManager ftpFileManager, File jsonFile, Config config, Context context){
//   // File tmpFile=Optional.ofNullable(downloadJsonFile(jsonFile,config,context,ftpFileManager));
//Optional<File> tmpFile= Optional.ofNullable(downloadJsonFile(jsonFile,config,context,ftpFileManager));
//return tmpFile.map(FileSyncService::tmpToJson);
//}
public  static boolean syncMediaFiles(File jsonFile, Config config, Context context, File baseFolder, MediaPlayerManager mp, String tempFileExtension) throws IOException {
    FtpConnectionManager ftpConnectionManager= new FtpConnectionManager();
    FtpFileManager ftpFileManager = new FtpFileManager(ftpConnectionManager.getFtpClient());
    if(!NetworkUtils.isNetworkConnected(context)){return false;}
    ftpConnectionManager.connect(config.getHost());
    ftpConnectionManager.login(config.getUserName(),config.getPassword());
        if (FileChecker.isFileExist(jsonFile)){
            if (isJsonChanged(jsonFile,ftpConnectionManager.getFtpClient())){
                if (!formPlaylistAndJson(ftpFileManager,jsonFile,config,context,baseFolder,mp,tempFileExtension))return false;
            }else {
if (!mp.isPlayerOnWork()){
    FileLogger.logError("syncMediaFiles", "isPlayerOnWork is false");
return false;}
            }//else json exist but not changed
        }else {
      if (!formPlaylistAndJson(ftpFileManager,jsonFile,config,context,baseFolder,mp,tempFileExtension))return false;
        }//JSON FILE EXISt IF END
return true;
}//syncMediaFiles
public  static boolean formPlaylistAndJson(FtpFileManager ftpFileManager, File jsonFile, Config config, Context context, File baseFolder, MediaPlayerManager mp, String tempFileExtension) throws IOException {
    if (updateJsonFromFtp(ftpFileManager,jsonFile,config,context,tempFileExtension)){
        ArrayList<MediaItem> mediaPlaylist= MediaItemHandler.createMediaItemPlaylist(JSONHandler.readJsonFromFile(jsonFile),baseFolder);
        if (mediaPlaylist.isEmpty()){
            FileLogger.logError("formPlaylistAndJson", "mediaPlayList isEmpty, isFileExist else part");
            return false;
        }else {
            if (!downloadAbsentMedia(mediaPlaylist,ftpFileManager,config,tempFileExtension))return false;
            deleteMissingMedia(mediaPlaylist);
            mp.startPlaylist(mediaPlaylist);
            return true;
        }
    }
   return false;
}
public static boolean deleteAllTmp(File baseFolder,String tempFileExtension){
List<File> tmpList = FileHandler.getAllTmpInDir(baseFolder, tempFileExtension);
if (tmpList.isEmpty()){
    FileLogger.log("deleteAllTmp", "list is empty");
    return true;}
    for (File file:
         tmpList) {
        if (FileHandler.deleteFile(file)){return false;}
    }
    return true;
}
public static void deleteMissingMedia( ArrayList<MediaItem> playlist){
    if (playlist.isEmpty())FileLogger.logError("deleteMissingMedia", "playlist is empty");
    Iterator<MediaItem> iterator= playlist.iterator();
    while (iterator.hasNext()){
        if (!iterator.next().getFile().exists()){
            iterator.remove();
        }
    }
}
public static boolean downloadAbsentMedia( ArrayList<MediaItem> playlist, FtpFileManager ftpFileManager, Config config, String tempFileExtension) throws IOException {
        if (playlist.isEmpty())FileLogger.logError("downloadAbsentMedia", "playlist is empty");
        ArrayList<MediaItem> downloadMedia;
        boolean success;
    downloadMedia=mediaToDownload(playlist);
    if (downloadMedia.isEmpty())FileLogger.logError("downloadAbsentMedia","mediaPlaylist isEmpty after itemsToDownload" );
    downloadMedia=existingFtpMedia(downloadMedia,ftpFileManager,config);
    if (downloadMedia.isEmpty())FileLogger.logError("downloadAbsentMedia","mediaPlaylist isEmpty after existingFtpMedia" );
    success= downloadMediaFiles(config,ftpFileManager,downloadMedia,tempFileExtension);
    FileLogger.log("downloadAbsentMedia","downloadMediaFiles: "+success);
    return success;
}
public static boolean downloadMediaFiles(Config config, FtpFileManager ftpFileManager, ArrayList<MediaItem> mediaPlaylist, String tempExtension) throws IOException {
        if (mediaPlaylist.isEmpty()) FileLogger.logError("downloadMediaFiles", "mediaPlaylist is empty");
ftpFileManager.moveCurrentDir(config.getMediaDirName());
    for (MediaItem item:mediaPlaylist
         ) {
        StringBuilder sb= new StringBuilder(item.getFile().getAbsolutePath());
        sb.append(tempExtension);
        if (!ftpFileManager.downloadFile(item.getName(),sb.toString()))return false;
    }
   return true;
}
public static ArrayList<MediaItem> mediaToDownload(ArrayList<MediaItem> mediaItems){
    return mediaItems.stream().filter(mediaItem -> mediaItem.getFile().exists()).collect(Collectors.toCollection(ArrayList::new));
}
public static ArrayList<MediaItem> existingFtpMedia(ArrayList<MediaItem> mediaItems, FtpFileManager ftpFileManager, Config config) throws IOException {
        ftpFileManager.moveCurrentDir(config.getMediaDirName());
     return  mediaItems.stream().filter(mediaItem -> ftpFileManager.fileExists(mediaItem.getName())).collect(Collectors.toCollection(ArrayList::new));
}
}//class

//if (tmpFile!=null&& tmpFile.exists()){
// FileLogger.log("syncMediaTmpToJson", "isSuccess: "+ tmpToJson(tmpFile));
//
// List<Map<String,Object>> mediaList= JSONHandler.readJsonFromFile(jsonFile);
// //   MediaItemHandler.createMediaItem(mediaList, baseFolder);
//}else {
//    return false;
//}
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
//
//    ftpFileManager.changeWorkingDirectory(config.getMediaDirName());
//}catch (IOException e){
//FileLogger.logError("SyncMediaFiles","Exception: "+ e.getMessage());
//return false;
//}
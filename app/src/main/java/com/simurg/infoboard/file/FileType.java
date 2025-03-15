package com.simurg.infoboard.file;
import android.media.MediaMetadataRetriever;

import com.simurg.infoboard.item.MediaItem;
import com.simurg.infoboard.log.FileLogger;
import java.io.File;
import java.io.IOException;

public class FileType {
    private static final String TAG= "FileType";
    public boolean isVideoExactly(File file) throws IOException {
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        try {
            mediaMetadataRetriever.setDataSource(file.getAbsolutePath());
           String hasVideo= mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_VIDEO);
            return "yes".equalsIgnoreCase(hasVideo);
        }catch (Exception e){
            FileLogger.logError(TAG, "Error isVideo method, mediaMetadataRetriever");
            return false;
        }
        finally{
            FileLogger.log(TAG,"mediaMetadataRetriever closed");
            mediaMetadataRetriever.release();
        }
    }

    public static boolean isVideo(File file){
        String fileName= file.getName().toLowerCase();
        return fileName.endsWith(".mp4");
    }
    public static boolean isImage(File file){
        String fileName= file.getName().toLowerCase();
        return fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(".png") || fileName.endsWith(".bmp");
    }
    public static boolean isText(File file){
        String fileName= file.getName().toLowerCase();
        return fileName.endsWith(".txt");
    }

    public static int DetectFileType(File file){
        if (isImage(file)){
            FileLogger.log(TAG,"IMAGE "+file.getAbsolutePath());
            return MediaItem.TYPE_IMAGE;
        }else if (isVideo(file)){
            FileLogger.log(TAG,"VIDEO "+file.getAbsolutePath());
            return MediaItem.TYPE_VIDEO;
        }else if (isText(file)){
            FileLogger.log("TAG", "TEXT "+ file.getAbsolutePath());
            return  MediaItem.TYPE_TEXT;
        }
        FileLogger.logError(TAG,"Unknown type of file "+ file.getAbsolutePath());
        return MediaItem.TYPE_UNKNOWN;
    }
//    public enum TypeOfFile{
//        VIDEO, IMAGE,TEXT, UNKNOWN
//    }
    //public static TypeOfFile DetectFileType1(File file){
//        if (isImage(file)){
//            FileLogger.log(TAG,"IMAGE "+file.getAbsolutePath());
//            return TypeOfFile.IMAGE;
//        }else if (isVideo(file)){
//            FileLogger.log(TAG,"VIDEO "+file.getAbsolutePath());
//            return TypeOfFile.VIDEO;
//        }else if (isText(file)){
//            FileLogger.log("TAG", "TEXT "+ file.getAbsolutePath());
//            return  TypeOfFile.TEXT;
//        }
//        FileLogger.logError(TAG,"Unknown type of file "+ file.getAbsolutePath());
//        return TypeOfFile.UNKNOWN;
//}
}

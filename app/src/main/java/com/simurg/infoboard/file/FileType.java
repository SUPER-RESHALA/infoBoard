package com.simurg.infoboard.file;
import android.media.MediaMetadataRetriever;

import com.simurg.infoboard.item.MediaItem;
import com.simurg.infoboard.log.FileLogger;
import com.simurg.infoboard.utils.mapUtils;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

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
    public static boolean isVideo(String fileName){
        fileName = fileName.toLowerCase();
        return fileName.endsWith(".mp4");
    }

    public static boolean isImage(String fileName){
        fileName = fileName.toLowerCase();
        return fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(".png") || fileName.endsWith(".bmp");
    }

    public static boolean isText(String fileName){
        fileName = fileName.toLowerCase();
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
//public static int DetectFileTypeOld(Map<String, Object> item){
//if (!item.containsValue("name")){
//    return MediaItem.TYPE_UNKNOWN;
//}else {
//    //T requireNonNull решить вопрос
// String name= Objects.requireNonNull(item.get("name")).toString();
//    if (isImage(name)){
//        FileLogger.log(TAG,"IMAGE "+item);
//        return MediaItem.TYPE_IMAGE;
//    }else if (isVideo(name)){
//        FileLogger.log(TAG,"VIDEO "+item );
//        return MediaItem.TYPE_VIDEO;
//    }else if (isText(name)){
//        FileLogger.log("TAG", "TEXT "+ item);
//        return  MediaItem.TYPE_TEXT;
//    }
//}
//    FileLogger.logError(TAG,"Unknown type of file "+ item);
//    return MediaItem.TYPE_UNKNOWN;
//}

    public static int DetectFileType(Map<String, Object> item) {
        if (!item.containsKey(MediaItem.nameStr) || item.get(MediaItem.nameStr) == null) {
            FileLogger.logError("DetectFileType", "Incorrect type ");
            return MediaItem.TYPE_UNKNOWN;
        }
        //TODO Возможен nullPointer
        String name = item.get(MediaItem.nameStr).toString();
        if (isImage(name)) {
            FileLogger.log(TAG, "IMAGE " + item);
            return MediaItem.TYPE_IMAGE;
        }
        if (isVideo(name)) {
            FileLogger.log(TAG, "VIDEO " + item);
            return MediaItem.TYPE_VIDEO;
        }
        if (isText(name)) {
            FileLogger.log(TAG, "TEXT " + item);
            return MediaItem.TYPE_TEXT;
        }
        FileLogger.logError(TAG, "Unknown type of file " + item);
        return MediaItem.TYPE_UNKNOWN;
    }
    public static int DetectFileTypeByString(String name) {
        if (name == null || name.isEmpty()) {
            return MediaItem.TYPE_UNKNOWN;
        }

        // Проверка на изображение
        if (isImage(name)) {
            FileLogger.log(TAG, "IMAGE " + name);
            return MediaItem.TYPE_IMAGE;
        }

        // Проверка на видео
        if (isVideo(name)) {
            FileLogger.log(TAG, "VIDEO " + name);
            return MediaItem.TYPE_VIDEO;
        }

        // Проверка на текстовый файл
        if (isText(name)) {
            FileLogger.log(TAG, "TEXT " + name);
            return MediaItem.TYPE_TEXT;
        }

        // Если тип не определён
        FileLogger.logError(TAG, "Unknown type of file " + name);
        return MediaItem.TYPE_UNKNOWN;
    }




//public static MediaItem mediaItem(Map<String, Object> item){
//
//}
}

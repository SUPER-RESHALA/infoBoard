package com.simurg.infoboard.item;

import com.simurg.infoboard.file.FileType;
import com.simurg.infoboard.log.FileLogger;

import java.util.Map;

public class MediaItemHandler {
    ///check object instanceof Integer
    /// @return (int)duration if object is int>0 or 0 otherwise
    public static int checkDuration(Object duration) {
        if (duration == null) {
            return 0;
        }
        if (duration instanceof Integer) {
            int value = (int) duration;  // Один раз приводим к int
            return Math.max(value, 0);  // Тернарный оператор вместо if-else
        }
        return 0;
    }
///check object instanceof String
/// @return (String)name if object is String or "DefaultValue" otherwise
    public static  String checkString(Object name){
       return name instanceof String? (String) name:"DefaultValue";
    }
    ///create MediaItem object appropriate to 1 of 3 types(video,text,image) or null if object incorrect
    ///@param jsonItem 1 jsonObject from json array: {name:example.mp4,duration:5}
    /// @return MediaItem=new Image,Text,Video(Item) or null, if object==MediaItem.TYPE_UNKNOWN
    public  static  MediaItem  createMediaItem(Map<String, Object> jsonItem){
        int duration= checkDuration(jsonItem.get(MediaItem.durationStr));
        String name= checkString(jsonItem.get(MediaItem.nameStr));
       switch (FileType.DetectFileType(jsonItem)){
           case MediaItem.TYPE_IMAGE:
               return new ImageItem(name,duration);
           case MediaItem.TYPE_VIDEO:
                   return new VideoItem(name);
           case MediaItem.TYPE_TEXT:
                   return new TextItem(name, duration);
           default:
               // TODO РЕШИТЬ ВОПРОС с NULL
               FileLogger.logError("createMediaItem", "Unknown file type: " + jsonItem);
               return  null;
       }
    }
}

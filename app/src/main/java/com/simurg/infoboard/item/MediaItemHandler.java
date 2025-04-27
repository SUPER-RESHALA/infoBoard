package com.simurg.infoboard.item;

import com.simurg.infoboard.file.FileType;
import com.simurg.infoboard.log.FileLogger;
import com.simurg.infoboard.mydate.CustomDate;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class MediaItemHandler {
    /**
     * Validates and parses a scheduled time string into a Date object.
     * This method attempts to parse the given time string. If parsing fails,
     * it logs an error and returns a default Date object set to the epoch time (0L).
     *
     * @param scheduledTime The time string to validate and parse.
     * @return A Date object representing the parsed time, or a default Date (epoch) if parsing fails.
     */
    public static Date validateScheduledTime(String scheduledTime){
        Date time;
        try {
             time  =CustomDate.parseTime(scheduledTime);
        } catch (ParseException e) {
            FileLogger.logError("validateScheduledTime", "ParseException invalid time:  "+ scheduledTime+"      "+e.getMessage());
             return new Date(0L);
          //  throw  new RuntimeException();
        }
       return time;
    }
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
    ///@param jsonItem 1 jsonObject from json array: {name:example.mp4,duration:5} and base app folder(example:files)
    /// @return MediaItem=new Image,Text,Video(Item) or null, if object==MediaItem.TYPE_UNKNOWN
    public  static MediaItem  createMediaItem(Map<String, Object> jsonItem, File baseFolder){
        int duration= checkDuration(jsonItem.get(MediaItem.durationStr));
        String name= checkString(jsonItem.get(MediaItem.nameStr));
        File mediaFile= new File(baseFolder, name);
        boolean isScheduled=jsonItem.containsKey(MediaItem.timeStr);
        //TODO Посмотреть этот момент по моему :new Date(0L); излишне в строке ниже если брать в учёт  public boolean isScheduled(MediaItem mediaItem)
        Date scheduledTime= validateScheduledTime(checkString(jsonItem.get(MediaItem.timeStr)));
        //Date scheduledTime= isScheduled?validateScheduledTime((String) jsonItem.get(MediaItem.timeStr)):new Date(0L);
        switch (FileType.DetectFileType(jsonItem)){
            case MediaItem.TYPE_IMAGE:
                return new ImageItem(mediaFile,duration,scheduledTime,isScheduled);
            case MediaItem.TYPE_VIDEO:
                return new VideoItem(mediaFile,scheduledTime,isScheduled);
            case MediaItem.TYPE_TEXT:
                return new TextItem(mediaFile, duration,scheduledTime,isScheduled);
            default:
                // TODO РЕШИТЬ ВОПРОС с NULL
                FileLogger.logError("createMediaItem", "Unknown file type: " + jsonItem);
                return  null;
        }
    }
public static ArrayList<MediaItem> createMediaItemPlaylist(List<Map<String,Object>> mediaList, File baseFolder){
        ArrayList<MediaItem> mediaPlaylist= new ArrayList<>();
    for (Map<String,Object> media:
            mediaList) {
        MediaItem newItem=createMediaItem(media,baseFolder);
        if (newItem!=null){
            mediaPlaylist.add(newItem);
        }
    }
return mediaPlaylist;
}



}

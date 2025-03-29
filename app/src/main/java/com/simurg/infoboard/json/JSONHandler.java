package com.simurg.infoboard.json;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.simurg.infoboard.file.FileType;
import com.simurg.infoboard.item.ImageItem;
import com.simurg.infoboard.item.MediaItem;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class JSONHandler {
    public static List<Map<String, Object>> readJsonFromString(String json) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> root = objectMapper.readValue(json, new TypeReference<>() {});
        return (List<Map<String, Object>>) root.get("items"); // Возвращаем массив объектов
    }
public static List<Map<String, Object>> readJsonFromFile(File jsonFile) throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    // Чтение JSON из файла
   return objectMapper.readValue(jsonFile, new TypeReference<List<Map<String, Object>>>() {});}

    public static  ArrayList<String> getNamesFromJson(File jsonFile) throws IOException {
ArrayList<String> names= new ArrayList<>();
        List<Map<String, Object>> json=  readJsonFromFile(jsonFile);
        for (Map<String, Object> obj: json) {
            //TODO requireNonNull решить вопрос с ними
            names.add(Objects.requireNonNull(obj.get("name")).toString());
        }
        return names;
    }

    public static ArrayList<String> getNamesFromJsonNoNull(File jsonFile) throws IOException {
        ArrayList<String> names = new ArrayList<>();
        List<Map<String, Object>> json = readJsonFromFile(jsonFile);  // Получение данных из JSON

        for (Map<String, Object> obj : json) {
            // Получение значения по ключу "name"
            Object nameObj = obj.get("name");

            // Проверяем, если "name" отсутствует или его значение null
            if (nameObj != null) {
                names.add(nameObj.toString());
            } else {
                //TODO ДОбавить обработчик в случае NULL или удалить метод
                // Если "name" отсутствует или равно null, можно пропустить или обработать ошибку
                System.out.println("Warning: Missing 'name' in object: " + obj);
            }
        }

        return names;
    }




//    public static List<MediaItem> parseJson(List<Map<String, Object>> json){
//        List<MediaItem> mediaItems= new ArrayList<>();
//for (Map<String, Object> item:json){
//    switch (FileType.DetectFileType(item)){
//        case MediaItem.TYPE_IMAGE:
//            mediaItems.add(new ImageItem(,))
//            break;
//        default:
//            break;
//    }
//
//
//}
//
//    }


}



package com.simurg.infoboard.utils;

import java.util.Map;

public class mapUtils {
    public static String stringValueOfMap(Map<String, Object> map){
        StringBuilder s= new StringBuilder();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            s.append(entry.getKey()).append("=").append(entry.getValue()).append("|");
        }
        s.append(";");
        return s.toString();
    }
}

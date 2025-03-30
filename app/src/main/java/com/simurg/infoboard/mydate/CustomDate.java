package com.simurg.infoboard.mydate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CustomDate {
        public static Date parseTime(String time) throws ParseException {
            SimpleDateFormat simpleDateFormat= new SimpleDateFormat("HH:mm", Locale.ROOT);
            return simpleDateFormat.parse(time);
        }
}

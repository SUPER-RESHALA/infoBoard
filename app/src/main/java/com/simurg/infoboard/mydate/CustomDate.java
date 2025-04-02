package com.simurg.infoboard.mydate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CustomDate {
        public static Date parseTime(String time) throws ParseException {
            SimpleDateFormat simpleDateFormat= new SimpleDateFormat("HH:mm", Locale.ROOT);
            return simpleDateFormat.parse(time);
        }
        public static Date scheduledTimeToCurrentDate(Date time){
            Calendar calendar= Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, time.getHours());
            calendar.set(Calendar.MINUTE, time.getMinutes());
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            return calendar.getTime();
        }
        public  static  Date getCurrentDate(){
            return new Date();
        }
}

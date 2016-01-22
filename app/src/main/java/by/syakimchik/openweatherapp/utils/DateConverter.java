package by.syakimchik.openweatherapp.utils;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Sergey on 11/2/2015.
 * @author Sergey Yakimchik
 */
public class DateConverter {

    public static String convertToLocalTime(String gmtDate){
        if(gmtDate!=null && !TextUtils.isEmpty(gmtDate)) {
            String fromTimeZone = "UTC";
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            dateFormat.setTimeZone(TimeZone.getTimeZone(fromTimeZone));
            Date parsedDate;
            try {
                parsedDate = dateFormat.parse(gmtDate);
            } catch (ParseException e) {
                e.printStackTrace();
                return "";
            }
            dateFormat = new SimpleDateFormat("MM.dd hh:mm aa");
            dateFormat.setTimeZone(Calendar.getInstance().getTimeZone());

            return dateFormat.format(parsedDate);
        }
        return "";
    }
}

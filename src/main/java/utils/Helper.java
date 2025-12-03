package utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Helper {

    public static String getDate(int dayModifier) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, dayModifier);
        return dateFormat.format(calendar.getTime());
    }
}

package com.bianco.raincoat;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by Grayson on 1/23/17.
 */

public class Weather {
    public final String dayOfWeek;
    public final String minTemp;
    public final String maxTemp;
    public final String humidity;
    public final String description;
    public final String iconURL;

    //constructor
    public Weather(long timeStamp, double minTemp, double maxTemp, double humidity, String
            description, String iconName) {
        // use NumberFormat to round to ints
        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        // don't allow for decimals
        numberFormat.setMaximumFractionDigits(0);
        dayOfWeek = convertTimeStampToDay(timeStamp);
        this.minTemp = numberFormat.format(minTemp) + "\u00B0F";
        this.maxTemp = numberFormat.format(maxTemp) + "\u00B0F";
        this.humidity = NumberFormat.getPercentInstance().format(humidity / 100.0);
        this.description = description;
        this.iconURL = "http://openweathermap.org/img/w/" + iconName + ".png";
    }

    // convert timestamp to day
    private static String convertTimeStampToDay(long timeStamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeStamp * 1000); // since timestamp is in sec.
        TimeZone timeZone = TimeZone.getDefault();
        // adjust for current timezone by adding/subtracting time off +- 0.00 time
        calendar.add(Calendar.MILLISECOND, timeZone.getOffset(calendar.getTimeInMillis()));

        SimpleDateFormat dateFormatter = new SimpleDateFormat("EEEE"); // EEEE = day of week
        return dateFormatter.format(calendar.getTime());
    }
}

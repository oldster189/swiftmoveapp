package com.oldster.swiftmove.app.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Old'ster on 4/12/2559.
 */

public class DateTimeValue {

    private String stringDate, stringTime, dateTime;
    private Date date = null;

    public DateTimeValue(String stringDate, String stringTime) {
        this.stringDate = stringDate;
        this.stringTime = stringTime;
        setDate();
    }

    public DateTimeValue(String dateTime) {
        this.dateTime = dateTime;
        setDate(dateTime);
    }


    private void setDate() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            date = format.parse(stringDate + " " + stringTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void setDate(String dateTime) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            date = format.parse(dateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public String getTime() {
        return stringTime;
    }

    public String getDate() {
        return (String) android.text.format.DateFormat.format("dd", date); //20
    }

    public String getMount() {
        return (String) android.text.format.DateFormat.format("MMM", date); //Jun
    }

    public String getYear() {
        String year = (String) android.text.format.DateFormat.format("yy", date); //2013
        int result = Integer.parseInt(year);
        return String.valueOf((result + 43));
    }


}

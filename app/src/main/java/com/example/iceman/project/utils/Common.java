package com.example.iceman.project.utils;

import android.app.Application;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by iceman on 20/10/2016.
 */

public class Common extends Application {
    public static final String DATE_SAVE_TO_DB = "yyyy-MM-dd";
    public static final String DATE_SHOW = "dd/MM/yyyy";

    public static Common _Instance;

    public static Common getInstance() {
        if (_Instance == null) {
            _Instance = new Common();
        }
        return _Instance;
    }

    public String getCurrentDate(String dateFormat) {
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        SimpleDateFormat df = new SimpleDateFormat(dateFormat);
        String strDate = df.format(date);

        return strDate;
    }

    public String getCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        return hour + ":" + minute;
    }

    public String formatDate(String strDate, String strFormatSrc, String strFormatDes) {
        String strResult = "";
        SimpleDateFormat df = new SimpleDateFormat(strFormatSrc);
        SimpleDateFormat df1 = new SimpleDateFormat(strFormatDes);
        try {
            Date date = df.parse(strDate);
            strResult = df1.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return strResult;
    }

    public Date parseStr2Date(String strDate,String dateFormat) {
        SimpleDateFormat df = new SimpleDateFormat(dateFormat);
        Date date = null;
        try {
            date = df.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }


}

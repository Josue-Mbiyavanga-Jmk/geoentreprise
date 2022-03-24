package com.iduyatech.geoentreprise.Utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class UtilTimeStampToDate {

    public  static String convert(Long timestamp)
    {
        long time_stamp = timestamp;
        //convert seconds to milliseconds
        Date date = new Date(time_stamp);
        // format of the date
        SimpleDateFormat jdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss z");
        jdf.setTimeZone(TimeZone.getTimeZone("GMT+1"));
        String my_date = jdf.format(date);

        return  my_date;
    }

    public static String getCurTime(){
        Date date =new Date(System.currentTimeMillis());
        SimpleDateFormat format =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
        String time = format.format(date);
        return time;
    }

    public  static  long convertToTimestamp(String value, String format)
    {
        long dat=0;  //declare timestamp

        try {
            DateFormat df = new SimpleDateFormat(format);
            dat=df.parse(value).getTime();
        }
        catch (ParseException e)
        {

        }

        return dat;
    }


    public  static String getTime()
    {

        //convert seconds to milliseconds
        Date date = new Date();
        // format of the date
        SimpleDateFormat jdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss z");
        jdf.setTimeZone(TimeZone.getTimeZone("GMT+1"));
        String my_date = jdf.format(date);

        return  my_date;
    }

    public  static String getTime(Date date)
    {
        // format of the date
        SimpleDateFormat jdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss z");
        jdf.setTimeZone(TimeZone.getTimeZone("GMT+1"));
        String my_date = jdf.format(date);

        return  my_date;
    }
    public  static String getDate()
    {

        //convert seconds to milliseconds
        Date date = new Date();
        // format of the date
        SimpleDateFormat jdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String my_date = jdf.format(date);

        return  my_date;
    }

    public  static String getDateSimple()
    {

        //convert seconds to milliseconds
        Date date = new Date();
        // format of the date
        SimpleDateFormat jdf = new SimpleDateFormat("yyyy-MM-dd");
        String my_date = jdf.format(date);

        return  my_date;
    }

    public  static String getDateDMY()
    {

        //convert seconds to milliseconds
        Date date = new Date();
        // format of the date
        SimpleDateFormat jdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        String my_date = jdf.format(date);

        return  my_date;
    }

    public static Date getDate(String format, String value) {
        Date date = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            date = sdf.parse(value);
            /*if (!value.equals(sdf.format(date))) {
                date = null;
            }*/
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        return date;
    }
    public  static Long getTimeStamp()
    {
        String time= UtilTimeStampToDate.getTime();
        Long time_stamp=UtilTimeStampToDate.getDate("dd-MM-yyyy HH:mm:ss z",time).getTime();

        return  time_stamp;
    }

    public  static Long getTimeStamp(String value)
    {
        Date date_stamp=UtilTimeStampToDate.getDate("yyyy-MM-dd",value);

      /*  String time= UtilTimeStampToDate.getTime(date_stamp);

        Long date=getTimeStamp(time);*/

        return  date_stamp.getTime();
    }

    public  static Long getTimeStampDayAdd(Long date)
    {
        Long time_stamp= date + 86400000;
        return  time_stamp;
    }

    public static Long getTimeSecondFormat(long time){
        String timeToChaine = String.valueOf(time);
        String chaineFormat = timeToChaine+"000";
        long timeInSecondFormat= Long.parseLong(chaineFormat);

        return timeInSecondFormat;
    }

    public static Boolean secondOrMillisecond(long time){
        boolean b=false;
        String longueur =String.valueOf(time);
        if(longueur.length()<13){
            b=true;
        } else {
            b=false;
        }

        return b;
    }
}

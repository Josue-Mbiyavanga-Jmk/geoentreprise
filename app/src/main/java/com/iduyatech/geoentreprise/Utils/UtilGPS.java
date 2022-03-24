package com.iduyatech.geoentreprise.Utils;

import com.iduyatech.geoentreprise.App.AppController;

import androidx.appcompat.app.AppCompatActivity;

public class UtilGPS {


    public static String getGpsData(AppCompatActivity activity)
    {

        AppController global=(AppController)activity.getApplicationContext();

        if (global.gp == null) {
            global.gp = new GPS(activity);

            return  global.gp.getLatitude() + "#" + global.gp.getLongitude();

        } else {
            return global.gp.getLatitude() + "#" + global.gp.getLongitude();
        }


    }

    public  static boolean verifyLtLg(String l)
    {
        boolean b=false;
        boolean c=false;

        String[] tab=l.split("#");

        if(Double.valueOf(tab[0])==0 && Double.valueOf(tab[1])==0)
        {
            return false;
        }


        if(Double.valueOf(tab[0])>=-90 && Double.valueOf(tab[0])<=90)
        {
            b=true;
        }

        if(Double.valueOf(tab[1])>=-180 && Double.valueOf(tab[1])<=180)
        {
            c=true;
        }

       return  b && c;
    }

    }




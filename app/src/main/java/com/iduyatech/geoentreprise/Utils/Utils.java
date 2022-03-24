package com.iduyatech.geoentreprise.Utils;


import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by AdbY on 28/05/2018.
 */

public class Utils {
    private static final String ALLOWED_CHARACTERS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String NUMERIQUE_CHARACTERS = "0123456789";
    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String getRandomString(final int sizeOfRandomString) {
        final Random random = new Random();
        final StringBuilder sb = new StringBuilder(sizeOfRandomString);
        for (int i = 0; i < sizeOfRandomString; ++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        return sb.toString();
    }

    public static String getRandomStringNumeric(final int sizeOfRandomString) {
        final Random random = new Random();
        final StringBuilder sb = new StringBuilder(sizeOfRandomString);
        for (int i = 0; i < sizeOfRandomString; ++i)
            sb.append(NUMERIQUE_CHARACTERS.charAt(random.nextInt(NUMERIQUE_CHARACTERS.length())));
        return sb.toString();
    }

    public static String getSha1String(String texteClaire) {
        String hash = null;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] bytes = texteClaire.getBytes("UTF-8");
            digest.update(bytes, 0, bytes.length);
            bytes = digest.digest();

            // This is ~55x faster than looping and String.formating()
            hash = bytesToHex(bytes).toLowerCase();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return hash;
    }

    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static Map<String, Object> objetSansNull(Object o) {
        Gson gson = new GsonBuilder().create();

        Map<String, Object> map = new Gson().fromJson(
                gson.toJson(o), new TypeToken<HashMap<String, Object>>() {
                }.getType()
        );

        return map;
    }

    /*public  static void initialiseKeysPreference()
    {

        String d1 = Preferences.get(Keys.PreferencesKeys.LAST_DATE_ACTE);
        if(d1==null)
        {
            Preferences.save(Keys.PreferencesKeys.LAST_DATE_ACTE,"2000-03-30 12:05:08.347075");
        }


        String d2 = Preferences.get(Keys.PreferencesKeys.LAST_DATE_CATEGORIE);
        if(d2==null)
        {
            Preferences.save(Keys.PreferencesKeys.LAST_DATE_CATEGORIE,"2000-03-30 12:05:08.347075");
        }

        String d3 = Preferences.get(Keys.PreferencesKeys.LAST_DATE_COMMUNE_SECTEUR);
        if(d3==null)
        {
            Preferences.save(Keys.PreferencesKeys.LAST_DATE_COMMUNE_SECTEUR,"2000-03-30 12:05:08.347075");
        }

        String d4 = Preferences.get(Keys.PreferencesKeys.LAST_DATE_FAIT);
        if(d4==null)
        {
            Preferences.save(Keys.PreferencesKeys.LAST_DATE_FAIT,"2000-03-30 12:05:08.347075");
        }


        String d5 = Preferences.get(Keys.PreferencesKeys.LAST_DATE_PROFESSION);
        if(d5==null)
        {
            Preferences.save(Keys.PreferencesKeys.LAST_DATE_PROFESSION,"2000-03-30 12:05:08.347075");
        }


        String d6 = Preferences.get(Keys.PreferencesKeys.LAST_DATE_QUARTIER_GROUPEMENT);
        if(d6==null)
        {
            Preferences.save(Keys.PreferencesKeys.LAST_DATE_QUARTIER_GROUPEMENT,"2000-03-30 12:05:08.347075");
        }

        String d7 = Preferences.get(Keys.PreferencesKeys.LAST_DATE_SECTEUR);
        if(d7==null)
        {
            Preferences.save(Keys.PreferencesKeys.LAST_DATE_SECTEUR,"2000-03-30 12:05:08.347075");
        }

        String d8 = Preferences.get(Keys.PreferencesKeys.LAST_DATE_UNITE);
        if(d8==null)
        {
            Preferences.save(Keys.PreferencesKeys.LAST_DATE_UNITE,"2000-03-30 12:05:08.347075");
        }

        String d9 = Preferences.get(Keys.PreferencesKeys.LAST_DATE_VILLE_TERRITOIRE);
        if(d9==null)
        {
            Preferences.save(Keys.PreferencesKeys.LAST_DATE_VILLE_TERRITOIRE,"2000-03-30 12:05:08.347075");
        }







    }*/

    public static Boolean isEmptyFields(Object[] objects){
        boolean b=false;
        for (Object o:objects)
        {
            if(o instanceof EditText)
            {
                String text= ((EditText)o).getText().toString().trim();
                if(text.isEmpty()){
                    ((EditText) o).setError("Remplir ce champ!");
                    b=true;
                }

            }
        }
        return b;
    }
}

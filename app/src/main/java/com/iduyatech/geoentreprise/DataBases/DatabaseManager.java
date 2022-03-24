package com.iduyatech.geoentreprise.DataBases;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.iduyatech.geoentreprise.Entites.ECategorie;
import com.iduyatech.geoentreprise.Entites.EEntreprise;
import com.iduyatech.geoentreprise.Entites.EProduit;
import com.iduyatech.geoentreprise.Entites.EStation;
import com.iduyatech.geoentreprise.Entites.EUserAgent;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;


import java.sql.SQLException;

/**
 * Created by kevin lukanga 01/05/2019.
 */
@SuppressLint("NewApi")
public class DatabaseManager<T> {
    private static Context context;
    @SuppressWarnings("rawtypes")
    static private DatabaseManager instance;
    private DatabaseHelper helper;

    private DatabaseManager(Context ctx) {
        helper = new DatabaseHelper(ctx);
    }

    @SuppressWarnings("rawtypes")
    static public void init(Context ctx) {
        context = ctx;
        if (null == instance) {
            instance = new DatabaseManager(ctx);
        }
    }

    @SuppressWarnings("rawtypes")
    static public DatabaseManager getInstance() {
        if (null == instance) {
            instance = new DatabaseManager(context);
        }
        return instance;
    }

   /* public static boolean isFileExiste() {
        try {
            new FileOutputStream(DatabaseHelper.DB_PATH
                    + DatabaseHelper.DATABASE_NAME);
            return true;
        } catch (FileNotFoundException e) {
            return false;
        }
    }*/

  /*  public static boolean isFileExist() {
        try {
            new FileOutputStream(DatabaseHelper.DB_PATH
                    + DatabaseHelper.DATABASE_NAME);
            return true;
        } catch (FileNotFoundException e) {
            return false;
        }
    }*/

    public static void clearStation() {
        try {
            TableUtils.clearTable(DatabaseManager.getInstance().getConnectionSource(), EStation.class);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void clearCategorie() {
        try {
            TableUtils.clearTable(DatabaseManager.getInstance().getConnectionSource(), ECategorie.class);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void clearProduit() {
        try {
            TableUtils.clearTable(DatabaseManager.getInstance().getConnectionSource(), EProduit.class);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void clearEntreprise() {
        try {
            TableUtils.clearTable(DatabaseManager.getInstance().getConnectionSource(), EEntreprise.class);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void clearUserAgent() {
        try {
            TableUtils.clearTable(DatabaseManager.getInstance().getConnectionSource(), EUserAgent.class);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public DatabaseHelper getHelper() {
        return helper;
    }

    public SQLiteDatabase getSQDatabase() {
        SQLiteDatabase sqDatabase = null;
        try {

            sqDatabase = helper.getWritableDatabase();
        } catch (Exception e) {
            Log.w("Erreur",  e + "");
        }
        return sqDatabase;
    }

    public void viderTable(Class<T> entity) {
        ConnectionSource connectionSource = getHelper().getConnectionSource();
        try {
            TableUtils.clearTable(connectionSource, entity);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ConnectionSource getConnectionSource() {
        ConnectionSource connectionSource = null;
        try {
            connectionSource = getHelper().getConnectionSource();
        } catch (Exception e) {
        }
        return connectionSource;
    }

    public SQLiteDatabase getSQLiteDatabase() {
        SQLiteDatabase sqDatabase = null;
        try {

            sqDatabase = helper.getWritableDatabase();
        } catch (Exception e) {
            Log.w("Erreur",  e + "");
        }
        return sqDatabase;
    }

    public void clearTable(Class<T> entity) {
        ConnectionSource connectionSource = getHelper().getConnectionSource();
        try {
            TableUtils.clearTable(connectionSource, entity);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

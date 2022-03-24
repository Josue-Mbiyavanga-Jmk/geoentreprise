package com.iduyatech.geoentreprise.DataBases;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.iduyatech.geoentreprise.Entites.ECategorie;
import com.iduyatech.geoentreprise.Entites.EEntreprise;
import com.iduyatech.geoentreprise.Entites.EProduit;
import com.iduyatech.geoentreprise.Entites.EStation;
import com.iduyatech.geoentreprise.Entites.EUser;
import com.iduyatech.geoentreprise.Entites.EUserAgent;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;


/**
 * Created by kevin lukanga 01/05/2019.
 */

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
    //DatabaseHelper
    private static final String TAG = DatabaseHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "internGeoEntreprise.db";
    private static final int DATABASE_VERSION = 1;

    //Data Access Object
    private Dao<EStation, String> eStationDao = null;
    private Dao<EUser, String> eUserDao = null;
    private Dao<EUserAgent, String> eUserAgentDao = null;
    private Dao<ECategorie, String> eCategorieDao = null;
    private Dao<EEntreprise, String> eEntrepriseDao = null;
    private Dao<EProduit, String> eProduitDao = null;



    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, EStation.class);
            TableUtils.createTable(connectionSource, EEntreprise.class);
            TableUtils.createTable(connectionSource, EProduit.class);
            TableUtils.createTable(connectionSource, EUser.class);
            TableUtils.createTable(connectionSource, EUserAgent.class);
            TableUtils.createTable(connectionSource, ECategorie.class);


        } catch (Exception e) {
            Log.e(TAG, "BD n'a pas été créée", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {

        }catch (Exception e) {
            Log.e(TAG, "BD n'a pas été créée", e);
            throw new RuntimeException(e);
        }
    }

    //customize dao action
   public Dao<EStation, String> getStationDao() {
        if (null == eStationDao) {
            try {
                eStationDao = getDao(EStation.class);
            } catch (java.sql.SQLException e) {
                e.printStackTrace();
            }
        }
        return eStationDao;
    }

    public Dao<EUser, String> getUserDao() {
        if (null == eUserDao) {
            try {
                eUserDao = getDao(EUser.class);
            } catch (java.sql.SQLException e) {
                e.printStackTrace();
            }
        }
        return eUserDao;
    }

    public Dao<EUserAgent, String> getUserAgentDao() {
        if (null == eUserAgentDao) {
            try {
                eUserAgentDao = getDao(EUserAgent.class);
            } catch (java.sql.SQLException e) {
                e.printStackTrace();
            }
        }
        return eUserAgentDao;
    }


    public Dao<ECategorie, String> getCategorieDao() {
        if (null == eCategorieDao) {
            try {
                eCategorieDao = getDao(ECategorie.class);
            } catch (java.sql.SQLException e) {
                e.printStackTrace();
            }
        }
        return eCategorieDao;
    }

    public Dao<EEntreprise, String> getEntrepriseDao() {
        if (null == eEntrepriseDao) {
            try {
                eEntrepriseDao = getDao(EEntreprise.class);
            } catch (java.sql.SQLException e) {
                e.printStackTrace();
            }
        }
        return eEntrepriseDao;
    }

    public Dao<EProduit, String> getProduitDao() {
        if (null == eProduitDao) {
            try {
                eProduitDao = getDao(EProduit.class);
            } catch (java.sql.SQLException e) {
                e.printStackTrace();
            }
        }
        return eProduitDao;
    }

}

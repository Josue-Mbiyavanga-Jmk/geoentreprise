package com.iduyatech.geoentreprise.Dao;

import com.iduyatech.geoentreprise.DataBases.DatabaseManager;
import com.iduyatech.geoentreprise.Entites.EStation;
import com.j256.ormlite.stmt.DeleteBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StationDao {

    public static boolean create(EStation o) {
        boolean ok = false;
        try {
            int nbr = DatabaseManager.getInstance().getHelper().getStationDao().create(o);

            if (nbr > 0) {
                ok = true;
            }
            return ok;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean update(EStation o) {
        boolean ok = false;
        try {
            int nbr = DatabaseManager.getInstance().getHelper().getStationDao().update(o);

            if (nbr > 0) {
                ok = true;
            }
            return ok;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static EStation get(String ref) {
        EStation item = null;
        try {
            List<EStation> dataList = DatabaseManager.getInstance().getHelper()
                    .getStationDao().queryForEq("_id", ref);
            if (dataList != null && !dataList.isEmpty()) {
                item = dataList.get(0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return item;
    }

    public static EStation get(int id) {
        EStation item = null;
        try {
            List<EStation> dataList = DatabaseManager.getInstance().getHelper()
                    .getStationDao().queryForEq("id", id);
            if (dataList != null && !dataList.isEmpty()) {
                item = dataList.get(0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return item;
    }

    public static List<EStation> getAll() {

        List<EStation> eItemList = new ArrayList<>();
        try {

            eItemList = DatabaseManager.getInstance().getHelper().getStationDao()
                    .queryBuilder().orderBy("id", true)
                    //.where().eq("", "")
                    .query();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return eItemList;
    }

    public static boolean delete(int Id) {
        boolean b = true;
        try {
            DeleteBuilder deleteBuilder = DatabaseManager.getInstance().getHelper().getStationDao().deleteBuilder();
            deleteBuilder.
                    where().eq("id",Id);
            deleteBuilder.delete();
        } catch (Exception e) {
            b = false;
            e.printStackTrace();
        }
        return b;
    }

    public static long count() {
        long count = 0;
        try {
            count = DatabaseManager.getInstance().getHelper()
                    .getStationDao().queryBuilder().countOf();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }
}

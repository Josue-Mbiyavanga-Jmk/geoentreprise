package com.iduyatech.geoentreprise.Dao;

import com.google.android.gms.tasks.Task;
import com.iduyatech.geoentreprise.DataBases.DatabaseManager;
import com.iduyatech.geoentreprise.Entites.EUser;
import com.j256.ormlite.stmt.DeleteBuilder;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDao {

    public static boolean create(EUser o) {
        boolean ok = false;
        try {
            int nbr = DatabaseManager.getInstance().getHelper().getUserDao().create(o);

            if (nbr > 0) {
                ok = true;
            }
            return ok;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean update(EUser o) {
        boolean ok = false;
        try {
            int nbr = DatabaseManager.getInstance().getHelper().getUserDao().update(o);

            if (nbr > 0) {
                ok = true;
            }
            return ok;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static EUser get(String ref) {
        EUser item = null;
        try {
            List<EUser> dataList = DatabaseManager.getInstance().getHelper()
                    .getUserDao().queryForEq("_id", ref);
            if (dataList != null && !dataList.isEmpty()) {
                item = dataList.get(0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return item;
    }

    public static EUser get(int id) {
        EUser item = null;
        try {
            List<EUser> dataList = DatabaseManager.getInstance().getHelper()
                    .getUserDao().queryForEq("id", id);
            if (dataList != null && !dataList.isEmpty()) {
                item = dataList.get(0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return item;
    }

    public static EUser getUser(EUser eUser) {
        try {
            EUser item = null;
            if (DatabaseManager.getInstance().getHelper().getUserDao() != null) {



               /* List<EUser> dataList = DatabaseManager.getInstance().getHelper().getUserDao()
                        .queryBuilder().orderBy("mail", true)
                        .where().eq("mail", eUser.getPseudo()+".sos")
                        .and()
                        .eq("password",eUser.getPassword())
                        .query();

                if (dataList != null && !dataList.isEmpty()) {
                    item = dataList.get(0);
                } */

            } else {
                return null;
            }

            return item;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static EUser getPseudo(String pseudo) {
        EUser item = null;
        try {
            List<EUser> dataList = DatabaseManager.getInstance().getHelper()
                    .getUserDao().queryForEq("pseudo", pseudo);
            if (dataList != null && !dataList.isEmpty()) {
                item = dataList.get(0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return item;
    }

    public static List<EUser> getAll() {

        List<EUser> eItemList = new ArrayList<>();
        try {

            eItemList = DatabaseManager.getInstance().getHelper().getUserDao()
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
            DeleteBuilder deleteBuilder = DatabaseManager.getInstance().getHelper().getUserDao().deleteBuilder();
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
                    .getUserDao().queryBuilder().countOf();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }
}

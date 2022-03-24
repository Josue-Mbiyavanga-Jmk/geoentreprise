package com.iduyatech.geoentreprise.Dao;

import com.iduyatech.geoentreprise.DataBases.DatabaseManager;
import com.iduyatech.geoentreprise.Entites.ECategorie;
import com.iduyatech.geoentreprise.Entites.EHelper;
import com.j256.ormlite.stmt.DeleteBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategorieDao {

    public static boolean create(ECategorie o) {
        boolean ok = false;
        try {
            int nbr = DatabaseManager.getInstance().getHelper().getCategorieDao().create(o);

            if (nbr > 0) {
                ok = true;
            }
            return ok;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean update(ECategorie o) {
        boolean ok = false;
        try {
            int nbr = DatabaseManager.getInstance().getHelper().getCategorieDao().update(o);

            if (nbr > 0) {
                ok = true;
            }
            return ok;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static ECategorie get(String ref) {
        ECategorie item = null;
        try {
            List<ECategorie> dataList = DatabaseManager.getInstance().getHelper()
                    .getCategorieDao().queryForEq("_id", ref);
            if (dataList != null && !dataList.isEmpty()) {
                item = dataList.get(0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return item;
    }


    public static ECategorie get(int id) {
        ECategorie item = null;
        try {
            List<ECategorie> dataList = DatabaseManager.getInstance().getHelper()
                    .getCategorieDao().queryForEq("id", id);
            if (dataList != null && !dataList.isEmpty()) {
                item = dataList.get(0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return item;
    }

    public static ECategorie getByName(String name) {
        ECategorie item = null;
        try {
            List<ECategorie> dataList = DatabaseManager.getInstance().getHelper()
                    .getCategorieDao().queryForEq("name", name);
            if (dataList != null && !dataList.isEmpty()) {
                item = dataList.get(0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return item;
    }

    public static List<ECategorie> getAll() {

        List<ECategorie> eItemList = new ArrayList<>();
        try {

            eItemList = DatabaseManager.getInstance().getHelper().getCategorieDao()
                    .queryBuilder().orderBy("id", true)
                    //.where().eq("", "")
                    .query();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return eItemList;
    }

    public static List<ECategorie> getAllWithEntrepriseCategorie(List<EHelper.EntrepriseCategorie>list) {
        List<ECategorie> eItemList = new ArrayList<>();
        try {
            for (EHelper.EntrepriseCategorie o:list){
                ECategorie uneCategorie = CategorieDao.get(o.getRef_categorie());
                //c'est ici que nous allons imposer que des produits en ligne
                    eItemList.add(uneCategorie);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return eItemList;
    }


    public static boolean delete(int Id) {
        boolean b = true;
        try {
            DeleteBuilder deleteBuilder = DatabaseManager.getInstance().getHelper().getCategorieDao().deleteBuilder();
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
                    .getCategorieDao().queryBuilder().countOf();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }
}

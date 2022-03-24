package com.iduyatech.geoentreprise.Dao;

import com.iduyatech.geoentreprise.DataBases.DatabaseManager;
import com.iduyatech.geoentreprise.Entites.ECategorie;
import com.iduyatech.geoentreprise.Entites.EEntreprise;
import com.iduyatech.geoentreprise.Entites.EHelper;
import com.iduyatech.geoentreprise.Entites.EProduit;
import com.j256.ormlite.stmt.DeleteBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProduitDao {

    public static boolean create(EProduit o) {
        boolean ok = false;
        try {
            int nbr = DatabaseManager.getInstance().getHelper().getProduitDao().create(o);

            if (nbr > 0) {
                ok = true;
            }
            return ok;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean update(EProduit o) {
        boolean ok = false;
        try {
            int nbr = DatabaseManager.getInstance().getHelper().getProduitDao().update(o);

            if (nbr > 0) {
                ok = true;
            }
            return ok;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static EProduit get(String ref) {
        EProduit item = null;
        try {
            List<EProduit> dataList = DatabaseManager.getInstance().getHelper()
                    .getProduitDao().queryForEq("_id", ref);
            if (dataList != null && !dataList.isEmpty()) {
                item = dataList.get(0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return item;
    }

    public static EProduit get(int id) {
        EProduit item = null;
        try {
            List<EProduit> dataList = DatabaseManager.getInstance().getHelper()
                    .getProduitDao().queryForEq("id", id);
            if (dataList != null && !dataList.isEmpty()) {
                item = dataList.get(0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return item;
    }

    public static EProduit getByName(String name) {
        EProduit item = null;
        try {
            List<EProduit> dataList = DatabaseManager.getInstance().getHelper()
                    .getProduitDao().queryForEq("name", name);
            if (dataList != null && !dataList.isEmpty()) {
                item = dataList.get(0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return item;
    }

    public static List<EProduit> getAll() {

        List<EProduit> eItemList = new ArrayList<>();
        try {

            eItemList = DatabaseManager.getInstance().getHelper().getProduitDao()
                    .queryBuilder().orderBy("id", true)
                    //.where().eq("", "")
                    .query();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return eItemList;
    }

    public static List<EProduit> getActiveAll() {

        List<EProduit> eItemList = new ArrayList<>();
        try {

            eItemList = DatabaseManager.getInstance().getHelper().getProduitDao()
                    .queryBuilder().orderBy("id", true)
                    .where().eq("statut", "1")
                    .query();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return eItemList;
    }

    public static List<EProduit> getAllWithProduitStation(List<EHelper.ProduitStation>list) {
        List<EProduit> eItemList = new ArrayList<>();
        try {
            for (EHelper.ProduitStation o:list){
                EProduit unProduit = ProduitDao.get(o.getRef_produit());
                //c'est ici que nous allons imposer que des produits en ligne
                if(unProduit.getStatut() == 1){
                    eItemList.add(unProduit);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return eItemList;
    }


    public static boolean delete(int Id) {
        boolean b = true;
        try {
            DeleteBuilder deleteBuilder = DatabaseManager.getInstance().getHelper().getProduitDao().deleteBuilder();
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
                    .getProduitDao().queryBuilder().countOf();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }
}

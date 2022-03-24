package com.iduyatech.geoentreprise.Entites;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.List;

@DatabaseTable(tableName = "tb_station")
public class EStation {

    @DatabaseField(columnName = "id", generatedId = true)
    private int id;
    @DatabaseField(columnName = "_id")
    private String _id;
    @DatabaseField(columnName = "name")
    private String name;
    @DatabaseField(columnName = "ref_lieu")
    private String ref_lieu;
    @DatabaseField(columnName = "ref_entreprise")
    private String ref_entreprise;
    @DatabaseField(columnName = "isSocialSiege")
    private Boolean isSocialSiege;
    private EHelper.Gps gps;
    @DatabaseField(columnName = "gps")
    private String saveGps;
    @DatabaseField(columnName = "statut")
    private int statut;
    @DatabaseField(columnName = "create_uid")
    private String create_uid;
    @DatabaseField(columnName = "update_uid")
    private String update_uid;
    @DatabaseField(columnName = "date_create")
    private Long date_create;
    @DatabaseField(columnName = "date_update")
    private Long date_update;
    @DatabaseField(columnName = "phone")
    private String phone;
    private EHelper.EntiteAdministrative entiteAdministrative;
    @DatabaseField(columnName = "entiteAdministrative")
    private String saveEntiteAdministrative;
    private List<?> listProduit;
    @DatabaseField(columnName = "listProduit")
    private String savelistProduit;
    /*@DatabaseField(columnName = "version")
    private int __v;*/
    //Entier 1 si c'est déjà en ligne et 0 sinon


    public EStation() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRef_lieu() {
        return ref_lieu;
    }

    public void setRef_lieu(String ref_lieu) {
        this.ref_lieu = ref_lieu;
    }

    public String getRef_entreprise() {
        return ref_entreprise;
    }

    public void setRef_entreprise(String ref_entreprise) {
        this.ref_entreprise = ref_entreprise;
    }

    public Boolean getSocialSiege() {
        return isSocialSiege;
    }

    public void setSocialSiege(Boolean socialSiege) {
        isSocialSiege = socialSiege;
    }

    public EHelper.Gps getGps() {
        return gps;
    }

    public void setGps(EHelper.Gps gps) {
        this.gps = gps;
    }

    public String getSaveGps() {
        return saveGps;
    }

    public void setSaveGps(String saveGps) {
        this.saveGps = saveGps;
    }

    public int getStatut() {
        return statut;
    }

    public void setStatut(int statut) {
        this.statut = statut;
    }

    public String getCreate_uid() {
        return create_uid;
    }

    public void setCreate_uid(String create_uid) {
        this.create_uid = create_uid;
    }

    public String getUpdate_uid() {
        return update_uid;
    }

    public void setUpdate_uid(String update_uid) {
        this.update_uid = update_uid;
    }

    public Long getDate_create() {
        return date_create;
    }

    public void setDate_create(Long date_create) {
        this.date_create = date_create;
    }

    public Long getDate_update() {
        return date_update;
    }

    public void setDate_update(Long date_update) {
        this.date_update = date_update;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public EHelper.EntiteAdministrative getEntiteAdministrative() {
        return entiteAdministrative;
    }

    public void setEntiteAdministrative(EHelper.EntiteAdministrative entiteAdministrative) {
        this.entiteAdministrative = entiteAdministrative;
    }

    public String getSaveEntiteAdministrative() {
        return saveEntiteAdministrative;
    }

    public void setSaveEntiteAdministrative(String saveEntiteAdministrative) {
        this.saveEntiteAdministrative = saveEntiteAdministrative;
    }

    public List<?> getListProduit() {
        return listProduit;
    }

    public void setListProduit(List<?> listProduit) {
        this.listProduit = listProduit;
    }

    public String getSavelistProduit() {
        return savelistProduit;
    }

    public void setSavelistProduit(String savelistProduit) {
        this.savelistProduit = savelistProduit;
    }

    /*public int get__v() {
        return __v;
    }

    public void set__v(int __v) {
        this.__v = __v;
    }*/
}

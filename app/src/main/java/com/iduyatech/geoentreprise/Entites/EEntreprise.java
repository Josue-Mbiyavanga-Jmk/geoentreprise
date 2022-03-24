package com.iduyatech.geoentreprise.Entites;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.List;

@DatabaseTable(tableName = "tb_entreprise")
public class EEntreprise {
    @DatabaseField(columnName = "id", generatedId = true)
    private int id;
    @DatabaseField(columnName = "_id")
    private String _id;
    @DatabaseField(columnName = "name")
    private String name;
    @DatabaseField(columnName = "email")
    private String email;
    @DatabaseField(columnName = "phone")
    private String phone;
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
    @DatabaseField(columnName = "idNat")
    private String idNat;
    @DatabaseField(columnName = "rccm")
    private String rccm;
    @DatabaseField(columnName = "urlLog")
    private String urlLog;
    @DatabaseField(columnName = "description")
    private String description;
    //non persistant
    private EUserAgent userAgent;
    private List<?> listCategorie;
    @DatabaseField(columnName = "saveListCategorie")
    private String saveListCategorie;
    //@DatabaseField(columnName = "version")
    //private int __v;

    public EEntreprise() {
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public String getIdNat() {
        return idNat;
    }

    public void setIdNat(String idNat) {
        this.idNat = idNat;
    }

    public String getRccm() {
        return rccm;
    }

    public void setRccm(String rccm) {
        this.rccm = rccm;
    }

    public String getUrlLog() {
        return urlLog;
    }

    public void setUrlLog(String urlLog) {
        this.urlLog = urlLog;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public EUserAgent getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(EUserAgent userAgent) {
        this.userAgent = userAgent;
    }

    public List<?> getListCategorie() {
        return listCategorie;
    }

    public void setListCategorie(List<?> listCategorie) {
        this.listCategorie = listCategorie;
    }

    public String getSaveListCategorie() {
        return saveListCategorie;
    }

    public void setSaveListCategorie(String saveListCategorie) {
        this.saveListCategorie = saveListCategorie;
    }

    /*public int get__v() {
        return __v;
    }

    public void set__v(int __v) {
        this.__v = __v;
    }*/
}
